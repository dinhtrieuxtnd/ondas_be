# API Stream Nhạc — Tài liệu kỹ thuật

## Mục lục

1. [Tổng quan](#1-tổng-quan)
2. [Endpoint](#2-endpoint)
3. [Kiến trúc & các thành phần](#3-kiến-trúc--các-thành-phần)
4. [Luồng xử lý](#4-luồng-xử-lý)
5. [HTTP Range Request là gì?](#5-http-range-request-là-gì)
6. [Tăng Play Count](#6-tăng-play-count)
7. [Các file đã thay đổi / tạo mới](#7-các-file-đã-thay-đổi--tạo-mới)
8. [Test với HTML Player](#8-test-với-html-player)
9. [Ví dụ request & response](#9-ví-dụ-request--response)
10. [Xử lý lỗi](#10-xử-lý-lỗi)

---

## 1. Tổng quan

API stream nhạc cho phép client (web/mobile player) phát nhạc trực tiếp qua backend thay vì truy cập MinIO public URL.

**Lợi ích so với cách dùng public URL cũ:**

| | Public MinIO URL | Stream qua backend |
|---|---|---|
| Xác thực | ❌ Ai cũng truy cập được | ✅ Yêu cầu JWT |
| Seek / tua nhạc | Tuỳ MinIO config | ✅ HTTP Range hỗ trợ |
| Đếm lượt nghe | ❌ Không kiểm soát | ✅ Tăng `playCount` tự động |
| Ẩn URL MinIO | ❌ URL lộ ra client | ✅ Backend proxy, client không thấy URL nội bộ |

---

## 2. Endpoint

```
GET /api/songs/{id}/stream
```

### Headers

| Header | Bắt buộc | Mô tả |
|---|---|---|
| `Authorization` | ✅ | `Bearer <JWT token>` |
| `Range` | ❌ | Byte range muốn tải, ví dụ: `bytes=0-65535` |

### Path variable

| Tên | Kiểu | Mô tả |
|---|---|---|
| `id` | `UUID` | ID của bài hát |

### Response Headers

| Header | Mô tả |
|---|---|
| `Accept-Ranges: bytes` | Thông báo server hỗ trợ Range request |
| `Content-Type` | MIME type của audio (vd: `audio/mpeg`) |
| `Content-Length` | Số byte trong response này |
| `Content-Range` | Chỉ có khi trả 206: `bytes start-end/total` |

### HTTP Status Codes

| Status | Khi nào |
|---|---|
| `200 OK` | Không có `Range` header, trả toàn bộ file |
| `206 Partial Content` | Có `Range` header hợp lệ, trả đoạn được yêu cầu |
| `401 Unauthorized` | Không có hoặc JWT không hợp lệ |
| `404 Not Found` | Bài hát không tồn tại hoặc đã bị ẩn (`active = false`) |

---

## 3. Kiến trúc & các thành phần

Thiết kế tuân thủ kiến trúc phân tầng của dự án:

```
SongController          (presentation)
        │
        ▼
SongServicePort  ◄──  SongService         (application)
        │                    │
        │             SongRepoPort ◄── SongAdapter  (infrastructure)
        │             StoragePort  ◄── MinioStorageAdapter
        ▼
SongStreamResponse      (application/dto/response)
```

### 3.1 `SongStreamResponse` (record mới)

```
src/main/java/com/example/ondas_be/application/dto/response/SongStreamResponse.java
```

Java record chứa toàn bộ metadata cần thiết để build HTTP response:

```java
public record SongStreamResponse(
    InputStream audioStream,   // byte stream mở sẵn từ MinIO
    long totalSize,            // tổng kích thước file (-1 nếu không biết)
    long rangeStart,           // byte đầu tiên sẽ gửi
    long rangeEnd,             // byte cuối cùng sẽ gửi (inclusive)
    String contentType,        // MIME type (vd: "audio/mpeg")
    boolean isPartial          // true → 206, false → 200
)
```

### 3.2 `StoragePort` — method mới

```java
InputStream getObjectStream(String bucket, String objectName, long offset, long length);
```

- `offset`: byte bắt đầu đọc
- `length`: số byte cần đọc; `-1` = đọc đến hết file
- MinIO SDK: `GetObjectArgs.builder().offset(offset).length(length)`

### 3.3 `SongRepoPort` — method mới

```java
void incrementPlayCount(UUID id);
```

Thực hiện UPDATE trực tiếp bằng JPQL để tránh race condition khi nhiều user nghe cùng lúc:

```sql
UPDATE SongModel s SET s.playCount = s.playCount + 1 WHERE s.id = :id
```

---

## 4. Luồng xử lý

```
Client gửi GET /api/songs/{id}/stream
           │
           ▼
[SecurityFilter] Kiểm tra JWT → 401 nếu thiếu/invalid
           │
           ▼
SongController.streamSong(id, rangeHeader)
           │
           ▼
SongService.streamSong()
    ├── 1. Tìm Song theo id → 404 nếu không tồn tại hoặc active=false
    ├── 2. extractObjectName(audioBucket, song.getAudioUrl())
    │       → Tách "songs/audio/uuid.mp3" từ URL đầy đủ
    ├── 3. Đọc totalSize từ song.getAudioSizeBytes()
    ├── 4. Parse Range header
    │       "bytes=start-end"  → rangeStart, rangeEnd
    │       "bytes=start-"     → rangeStart, rangeEnd = totalSize - 1
    │       null / blank       → full file (rangeStart=0, rangeEnd=totalSize-1)
    ├── 5. rangeStart == 0 → songRepoPort.incrementPlayCount(id)
    ├── 6. storagePort.getObjectStream(bucket, objectName, rangeStart, length)
    │       → Mở kết nối đến MinIO, trả về InputStream
    └── 7. Return SongStreamResponse
           │
           ▼
SongController build HTTP response:
    ├── isPartial=true  → 206 + Content-Range header
    └── isPartial=false → 200
```

---

## 5. HTTP Range Request là gì?

HTTP Range cho phép client tải **một phần** của file thay vì toàn bộ. Đây là nền tảng để:

- **Seek (tua nhạc)**: player tua đến giây thứ 60 → gửi `Range: bytes=1440000-` (giả sử bitrate 192kbps)
- **Resume**: mạng bị ngắt ở byte 500000 → tiếp tục từ `Range: bytes=500000-`
- **Adaptive buffering**: player chỉ tải trước 1-2 giây, tiết kiệm băng thông

**Ví dụ tua đến phút 1:30 trong file MP3 128kbps:**
```
128kbps = 16KB/s → 1:30 = 90s → byte ≈ 1,440,000
Range: bytes=1440000-
```

Server trả về `206 Partial Content` với header:
```
Content-Range: bytes 1440000-3200000/3200000
Content-Length: 1760001
```

---

## 6. Tăng Play Count

**Quy tắc**: `playCount` chỉ tăng khi `rangeStart == 0`.

**Tại sao?**

| Trường hợp | rangeStart | Tăng? |
|---|---|---|
| Nghe từ đầu (lần đầu load) | 0 | ✅ |
| Tua đến giây 30 | > 0 | ❌ |
| Nghe lại từ đầu | 0 | ✅ |
| Buffer thêm (bytes=500000-) | 500000 | ❌ |

Điều này đảm bảo mỗi lần user bắt đầu nghe từ đầu mới tính 1 lượt, tránh tính nhiều lần khi player chia nhỏ request.

**Implementation dùng `@Modifying @Query` để atomic update:**
```java
// SongJpaRepo.java
@Modifying
@Query("UPDATE SongModel s SET s.playCount = s.playCount + 1 WHERE s.id = :id")
void incrementPlayCount(@Param("id") UUID id);
```

Lý do dùng JPQL UPDATE thay vì read-modify-save:
- Tránh lost update khi nhiều request đồng thời
- Một câu SQL → hiệu năng tốt hơn

---

## 7. Các file đã thay đổi / tạo mới

### Tạo mới

| File | Mô tả |
|---|---|
| `application/dto/response/SongStreamResponse.java` | Java record chứa stream metadata |

### Sửa đổi

| File | Thay đổi |
|---|---|
| `application/service/port/StoragePort.java` | Thêm method `getObjectStream` |
| `application/service/port/SongServicePort.java` | Thêm method `streamSong` |
| `application/service/impl/SongService.java` | Implement `streamSong` + helper `resolveContentType` |
| `domain/repoport/SongRepoPort.java` | Thêm method `incrementPlayCount` |
| `infrastructure/persistence/jparepo/SongJpaRepo.java` | Thêm `@Modifying @Query` incrementPlayCount |
| `infrastructure/persistence/adapter/SongAdapter.java` | Implement `incrementPlayCount` |
| `infrastructure/storage/MinioStorageAdapter.java` | Implement `getObjectStream` dùng MinIO SDK |
| `presentation/controller/SongController.java` | Thêm endpoint `GET /{id}/stream` |

---

## 8. Test với HTML Player

File `stream-test.html` được tích hợp sẵn vào Spring Boot static resources, truy cập không cần cài đặt thêm gì.

### 8.1 Khởi động

1. Đảm bảo MinIO và PostgreSQL đang chạy (Docker):
   ```bash
   docker compose -f docker-compose.dev.yml up -d
   ```

2. Chạy backend:
   ```bash
   mvn spring-boot:run "-Dspring-boot.run.profiles=dev"
   ```

3. Mở browser, truy cập:
   ```
   http://localhost:8080/stream-test.html
   ```

> **Quan trọng**: Phải mở qua URL `http://`, không double-click file hay drag vào browser.  
> Lý do: mở từ `file://` sẽ bị CORS block do browser gán origin là `null`.

---

### 8.2 Lấy JWT Token

Gọi API login để lấy token:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "user@example.com", "password": "yourpassword"}'
```

Response trả về:
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "..."
  }
}
```

Copy giá trị `accessToken` (không cần "Bearer "), dán vào ô **JWT Token** trong HTML player.

---

### 8.3 Lấy Song ID

Gọi API danh sách bài hát:

```bash
curl -H "Authorization: Bearer <token>" \
  http://localhost:8080/api/songs?page=0&size=10
```

Hoặc tra trực tiếp trong database:
```sql
SELECT id, title, audio_size_bytes FROM songs LIMIT 10;
```

---

### 8.4 Các nút chức năng

| Nút | Chức năng |
|---|---|
| **▶ Stream & Play** | Bắt đầu stream dùng **Media Source Extensions** — tự động fetch từng chunk, phát ngay khi có đủ buffer đầu tiên |
| **⏹ Stop** | Dừng stream và giải phóng bộ nhớ |
| **🔍 Test Headers** | Gửi request Range nhỏ, abort ngay, chỉ đọc response headers để kiểm tra `206`, `Content-Range`, `Accept-Ranges` mà không tải dữ liệu |
| **🗑 Clear** | Xoá log |

**Chunk size**: điều chỉnh kích thước mỗi Range request (mặc định 256 KB). Tăng lên nếu mạng tốt, giảm xuống nếu muốn thấy nhiều request hơn trong log.

---

### 8.5 Kết quả mong đợi

**Khi nhấn ▶ Stream & Play thành công:**

```
[10:30:01] ▶ Bắt đầu MSE stream | MIME: audio/mpeg
[10:30:01] → GET chunk bytes=0-262143
[10:30:01] Total size: 4.21 MB
[10:30:01] ← 206 chunk 256.0 KB | offset: 256 KB
[10:30:02] → GET chunk bytes=262144-524287
[10:30:02] ← 206 chunk 256.0 KB | offset: 512 KB
...
```

Audio player hiện ra phía dưới, thanh progress bar hiển thị % đã tải.

**Khi nhấn 🔍 Test Headers thành công:**

```
[10:30:05] → GET .../stream (headers only, Range: bytes=0-262143)
[10:30:05] ← 206 Partial Content
[10:30:05]    Content-Type: audio/mpeg
[10:30:05]    Content-Length: 262144
[10:30:05]    Content-Range: bytes 0-262143/4415832
[10:30:05]    Accept-Ranges: bytes
```

---

### 8.6 Fallback tự động

Nếu browser không hỗ trợ **Media Source Extensions** với `audio/mpeg` (hiếm, thường xảy ra trên Safari cũ), player tự động chuyển sang tải toàn bộ file về blob:

```
[10:30:01] ⚠ audio/mpeg không được MSE hỗ trợ trong browser này, dùng blob fallback...
[10:30:03] ✓ Blob loaded: 4.21 MB
```

---

### 8.7 Test bằng curl (không cần browser)

```bash
# Kiểm tra headers nhanh
curl -i -H "Authorization: Bearer <token>" \
     -H "Range: bytes=0-65535" \
     http://localhost:8080/api/songs/<song-id>/stream \
     --output /dev/null

# Tải về file để nghe thử
curl -H "Authorization: Bearer <token>" \
     http://localhost:8080/api/songs/<song-id>/stream \
     -o test.mp3

# Test không có JWT → phải trả 401
curl -i http://localhost:8080/api/songs/<song-id>/stream
```

---

## 9. Ví dụ request & response

### 9.1 Phát toàn bộ bài hát

**Request:**
```http
GET /api/songs/550e8400-e29b-41d4-a716-446655440000/stream
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Response:**
```http
HTTP/1.1 200 OK
Content-Type: audio/mpeg
Content-Length: 3200000
Accept-Ranges: bytes

<binary audio data>
```

### 9.2 Tua đến giữa bài (Range request)

**Request:**
```http
GET /api/songs/550e8400-e29b-41d4-a716-446655440000/stream
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Range: bytes=1440000-1504000
```

**Response:**
```http
HTTP/1.1 206 Partial Content
Content-Type: audio/mpeg
Content-Length: 64001
Content-Range: bytes 1440000-1504000/3200000
Accept-Ranges: bytes

<binary audio data — 64001 bytes>
```

### 9.3 Tải từ vị trí hiện tại đến hết

**Request:**
```http
GET /api/songs/550e8400-e29b-41d4-a716-446655440000/stream
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Range: bytes=2000000-
```

**Response:**
```http
HTTP/1.1 206 Partial Content
Content-Type: audio/mpeg
Content-Length: 1200001
Content-Range: bytes 2000000-3200000/3200001
Accept-Ranges: bytes

<binary audio data>
```

### 9.4 Lỗi — không có JWT

**Response:**
```http
HTTP/1.1 401 Unauthorized
Content-Type: application/json

{
  "success": false,
  "message": "Unauthorized",
  "data": null
}
```

### 9.5 Lỗi — bài hát không tồn tại

**Response:**
```http
HTTP/1.1 404 Not Found
Content-Type: application/json

{
  "success": false,
  "message": "Song not found with id: 550e8400-e29b-41d4-a716-446655440000",
  "data": null
}
```

---

## 10. Xử lý lỗi

| Tình huống | Xử lý |
|---|---|
| Song không tìm thấy | Throw `SongNotFoundException` → GlobalExceptionHandler → 404 |
| Song bị ẩn (`active=false`) | Throw `SongNotFoundException` (không tiết lộ lý do) → 404 |
| `audioSizeBytes` null | Trả 200 full stream, không hỗ trợ Range |
| Range header không hợp lệ | Fallback về full stream (200), không throw lỗi |
| MinIO lỗi khi stream | `IllegalStateException` → 500 Internal Server Error |
| JWT hết hạn / thiếu | Spring Security filter trả 401 trước khi vào controller |
