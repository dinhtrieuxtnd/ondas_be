# Project Instructions — Ondas Backend (Spring Boot)

> **Phạm vi áp dụng**: Toàn bộ project `ondas_be`
> **Stack**: Spring Boot 3.x + Java 17 + PostgreSQL + JWT + MinIO/S3
> **Kiến trúc**: Onion Architecture — 4 tầng: `domain` → `application` → `infrastructure` → `presentation`

---

## 1. TỔNG QUAN DỰ ÁN

Ondas là ứng dụng nghe nhạc trực tuyến. Backend cung cấp REST API cho:

| Client        | Mô tả                                      |
|---------------|--------------------------------------------|
| Mobile App    | Flutter — User: nghe nhạc, playlist, yêu thích |
| Admin Web     | Flutter Web — Quản lý bài hát, user, thống kê |

**Package gốc**: `com.example.ondas` (điều chỉnh theo cấu hình dự án thực tế)

---

## 2. CẤU TRÚC THƯ MỤC — BẮT BUỘC TUÂN THỦ

```
src/main/java/com/example/ondas/
│
├── presentation/                          # Tầng giao diện / API
│   ├── controller/                        # REST Controller
│   │   ├── AuthController.java
│   │   ├── SongController.java
│   │   └── ...
│   └── advice/                            # Exception handler toàn cục
│       └── GlobalExceptionHandler.java
│
├── application/                           # Tầng nghiệp vụ
│   ├── service/
│   │   ├── port/                          # Interface (contract) của service
│   │   │   ├── AuthServicePort.java
│   │   │   ├── SongServicePort.java
│   │   │   └── ...
│   │   └── impl/                          # Implementation của service
│   │       ├── AuthService.java
│   │       ├── SongService.java
│   │       └── ...
│   └── dto/
│       ├── common/                        # DTO dùng chung
│       │   ├── PageResultDto.java
│       │   └── GetListQueryDto.java
│       ├── request/                       # DTO cho request body
│       │   ├── LoginDto.java
│       │   ├── CreateSongDto.java
│       │   └── ...
│       └── response/                      # DTO cho response body
│           ├── UserDto.java
│           ├── SongDto.java
│           └── ...
│
├── infrastructure/                        # Tầng truy cập dữ liệu & dịch vụ ngoài
│   ├── persistence/
│   │   ├── adapter/                       # implements RepoPort từ domain
│   │   │   ├── UserAdapter.java
│   │   │   ├── SongAdapter.java
│   │   │   └── ...
│   │   ├── model/                         # JPA Entity (có annotation)
│   │   │   ├── UserModel.java
│   │   │   ├── SongModel.java
│   │   │   └── ...
│   │   └── jparepo/                       # Spring Data JPA repository
│   │       ├── UserJpaRepo.java
│   │       ├── SongJpaRepo.java
│   │       └── ...
│   ├── security/                          # JWT, Spring Security config
│   ├── storage/                           # Upload file (MinIO / S3)
│   └── websocket/                         # WebSocket (nếu có real-time)
│
└── domain/                                # Tầng nghiệp vụ thuần — KHÔNG phụ thuộc framework
    ├── entity/                            # Domain Entity thuần Java
    │   ├── User.java
    │   ├── Song.java
    │   └── ...
    └── repoport/                          # Repository interface (Port)
        ├── UserRepoPort.java
        ├── SongRepoPort.java
        └── ...

src/main/resources/
├── application.yml
└── db/migration/                          # Flyway migration scripts
    ├── V1__init_schema.sql
    └── ...

src/test/java/com/example/ondas/
├── unit/                                  # Unit test (Service, Adapter)
└── integration/                           # Integration test (API endpoint)
```

---

## 3. NGUYÊN TẮC PHÂN TẦNG — QUAN TRỌNG NHẤT

### 3.1 Dependency rule (Onion Architecture)

```
presentation  →  application  →  domain
infrastructure                →  domain
```

- **`domain`**: KHÔNG import bất kỳ package Spring / JPA / Lombok nào. Thuần Java.
- **`application`**: Import domain, KHÔNG import JPA / persistence.
- **`infrastructure`**: Import domain và Spring/JPA. Implements các Port từ domain.
- **`presentation`**: Import application (DTO, ServicePort). Không gọi trực tiếp repository.

### 3.2 Quy tắc giao tiếp giữa các tầng

| Từ tầng       | Được gọi                          | KHÔNG được gọi              |
|---------------|-----------------------------------|-----------------------------|
| `presentation`| `application/service/port/*`     | `infrastructure`, `domain/entity` trực tiếp |
| `application` | `domain/repoport/*`              | `infrastructure/persistence/*` |
| `infrastructure` | `domain/entity/*`, `domain/repoport/*` | `application/service/*` |

> ⚠️ **TUYỆT ĐỐI KHÔNG** inject `UserJpaRepo` vào Service. Service chỉ inject `UserRepoPort`.

---

## 4. NAMING CONVENTION

### 4.1 Class

| Loại                        | Convention                  | Ví dụ                            |
|-----------------------------|-----------------------------|----------------------------------|
| Domain Entity               | `PascalCase` (danh từ số ít)| `User`, `Song`, `Playlist`       |
| JPA Model                   | `PascalCase + Model`        | `UserModel`, `SongModel`         |
| Repository Port (interface) | `PascalCase + RepoPort`     | `UserRepoPort`, `SongRepoPort`   |
| JPA Repository              | `PascalCase + JpaRepo`      | `UserJpaRepo`, `SongJpaRepo`     |
| Persistence Adapter         | `PascalCase + Adapter`      | `UserAdapter`, `SongAdapter`     |
| Service Port (interface)    | `PascalCase + ServicePort`  | `AuthServicePort`, `SongServicePort` |
| Service Implementation      | `PascalCase + Service`      | `AuthService`, `SongService`     |
| Controller                  | `PascalCase + Controller`   | `AuthController`, `SongController` |
| DTO Request                 | `Verb + Noun + Dto`         | `LoginDto`, `CreateSongDto`, `UpdateProfileDto` |
| DTO Response                | `Noun + Dto`                | `UserDto`, `SongDto`, `AuthResponseDto` |
| Custom Exception            | `PascalCase + Exception`    | `NotFoundException`, `DuplicateSongException` |

### 4.2 Package & File

- Package: `lowercase`, phân cách bằng dấu chấm: `com.example.ondas.application.service`
- File Java: `PascalCase.java` — khớp với tên class

### 4.3 Database & API

| Loại            | Convention       | Ví dụ                        |
|-----------------|------------------|------------------------------|
| Tên bảng        | `snake_case` số nhiều | `users`, `songs`, `playlists` |
| Tên cột         | `snake_case`     | `created_at`, `artist_id`    |
| API path        | `kebab-case`     | `/api/songs`, `/api/favorite-songs` |
| Migration file  | `V{n}__{mô_tả}.sql` | `V1__init_schema.sql`      |

---

## 5. CODE PATTERN BẮT BUỘC

### 5.1 Domain Entity — Thuần Java

```java
// domain/entity/Song.java
// KHÔNG có annotation Spring, JPA, Lombok
public class Song {
    private Long id;
    private String title;
    private Long artistId;
    private Long albumId;  // nullable — single track
    private String audioUrl;
    private Integer durationSeconds;

    // Constructor, getter, setter viết tay hoặc dùng Java record
    public Song(Long id, String title, Long artistId, Long albumId,
                String audioUrl, Integer durationSeconds) {
        this.id = id;
        this.title = title;
        this.artistId = artistId;
        this.albumId = albumId;
        this.audioUrl = audioUrl;
        this.durationSeconds = durationSeconds;
    }

    // business logic methods (nếu có)
    public boolean isSingle() {
        return this.albumId == null;
    }

    // getters...
}
```

### 5.2 Repository Port — Interface ở Domain

```java
// domain/repoport/SongRepoPort.java
public interface SongRepoPort {
    Song save(Song song);
    Optional<Song> findById(Long id);
    Page<Song> findAll(Pageable pageable);
    void deleteById(Long id);
    boolean existsByTitleAndArtistId(String title, Long artistId);
}
```

### 5.3 JPA Model — Với annotation, có converter

```java
// infrastructure/persistence/model/SongModel.java
@Entity
@Table(name = "songs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SongModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "artist_id", nullable = false)
    private Long artistId;

    @Column(name = "album_id")
    private Long albumId;

    @Column(name = "audio_url", nullable = false)
    private String audioUrl;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    // Converter: Model ↔ Domain
    public Song toDomain() {
        return new Song(id, title, artistId, albumId, audioUrl, durationSeconds);
    }

    public static SongModel fromDomain(Song song) {
        return SongModel.builder()
                .id(song.getId())
                .title(song.getTitle())
                .artistId(song.getArtistId())
                .albumId(song.getAlbumId())
                .audioUrl(song.getAudioUrl())
                .durationSeconds(song.getDurationSeconds())
                .build();
    }
}
```

### 5.4 Persistence Adapter — Implements RepoPort

```java
// infrastructure/persistence/adapter/SongAdapter.java
@Component
@RequiredArgsConstructor
public class SongAdapter implements SongRepoPort {
    private final SongJpaRepo songJpaRepo;

    @Override
    public Song save(Song song) {
        SongModel model = SongModel.fromDomain(song);
        return songJpaRepo.save(model).toDomain();
    }

    @Override
    public Optional<Song> findById(Long id) {
        return songJpaRepo.findById(id).map(SongModel::toDomain);
    }

    @Override
    public Page<Song> findAll(Pageable pageable) {
        return songJpaRepo.findAll(pageable).map(SongModel::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        songJpaRepo.deleteById(id);
    }

    @Override
    public boolean existsByTitleAndArtistId(String title, Long artistId) {
        return songJpaRepo.existsByTitleAndArtistId(title, artistId);
    }
}
```

### 5.5 Service — Inject Port, không inject JPA trực tiếp

```java
// application/service/impl/SongService.java
@Service
@RequiredArgsConstructor
public class SongService implements SongServicePort {
    private final SongRepoPort songRepoPort;    // ✅ inject port
    private final ArtistRepoPort artistRepoPort;

    @Override
    public SongDto createSong(CreateSongDto request) {
        // Validate business rule
        if (!artistRepoPort.existsById(request.getArtistId())) {
            throw new NotFoundException("Artist not found: " + request.getArtistId());
        }
        if (songRepoPort.existsByTitleAndArtistId(request.getTitle(), request.getArtistId())) {
            throw new DuplicateSongException("Song already exists for this artist");
        }

        Song song = new Song(null, request.getTitle(), request.getArtistId(),
                             request.getAlbumId(), request.getAudioUrl(),
                             request.getDurationSeconds());
        Song saved = songRepoPort.save(song);
        return SongDto.from(saved);
    }
}
```

### 5.6 Controller — Trả về ApiResponse chuẩn

```java
// presentation/controller/SongController.java
@RestController
@RequestMapping("/api/songs")
@RequiredArgsConstructor
public class SongController {
    private final SongServicePort songServicePort;  // ✅ inject port

    @PostMapping
    public ResponseEntity<ApiResponse<SongDto>> createSong(
            @Valid @RequestBody CreateSongDto request) {
        SongDto song = songServicePort.createSong(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(song));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SongDto>> getSong(@PathVariable Long id) {
        SongDto song = songServicePort.getSongById(id);
        return ResponseEntity.ok(ApiResponse.success(song));
    }
}
```

---

## 6. QUY TẮC MAPPER (DOMAIN ↔ DTO)

Dự án có 2 loại conversion cần phân biệt rõ:

| Loại | Từ → Đến | Vị trí đặt |
|------|----------|------------|
| **Model ↔ Domain** | `SongModel` ↔ `Song` | Method trong `JpaModel` (`toDomain` / `fromDomain`) |
| **Domain → DTO** | `Song` → `SongDto` | Tùy theo mức độ tái sử dụng (xem quy tắc dưới) |

### 6.1 Quy tắc

```
- Map dùng ở NHIỀU NƠI  →  tạo class XxxMapper trong application/mapper/
- Map chỉ dùng 1 LẦN    →  map thủ công tại chỗ qua constructor hoặc Builder
```

> ⚠️ **KHÔNG** ép tạo Mapper riêng cho mọi DTO — chỉ tạo khi thực sự cần tái sử dụng.

### 6.2 Map thủ công (chỉ dùng 1 lần) — mặc định

Viết trực tiếp inline trong Service bằng constructor hoặc Builder:

```java
// Trong SongService.createSong() — map chỉ dùng ở đây → viết thủ công
Song saved = songRepoPort.save(song);

// Dùng constructor
return new SongDto(saved.getId(), saved.getTitle(), saved.getArtistId());

// Hoặc dùng Builder (nếu DTO có @Builder)
return SongDto.builder()
        .id(saved.getId())
        .title(saved.getTitle())
        .artistId(saved.getArtistId())
        .build();
```

### 6.3 Class Mapper riêng (dùng nhiều nơi)

Khi cùng một conversion được dùng ở ≥ 2 nơi khác nhau, tách ra class riêng:

```
application/
├── dto/
├── service/
└── mapper/                        # Chỉ tạo khi thực sự tái sử dụng
    ├── SongMapper.java
    └── UserMapper.java
```

```java
// application/mapper/SongMapper.java
// Tạo vì SongDto dùng trong cả SongService và PlaylistService
public class SongMapper {

    public static SongDto toDto(Song song) {
        return SongDto.builder()
                .id(song.getId())
                .title(song.getTitle())
                .artistId(song.getArtistId())
                .durationSeconds(song.getDurationSeconds())
                .build();
    }
}
```

```java
// SongService.java
return SongMapper.toDto(saved);

// PlaylistService.java — reuse cùng mapper
songs.stream().map(SongMapper::toDto).toList();
```

---


## 7. API RESPONSE FORMAT

Mọi API đều trả về format thống nhất `ApiResponse<T>`:

```java
// application/dto/common/ApiResponse.java
@Data
@Builder
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message("OK")
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .build();
    }
}
```

**Response mẫu — thành công:**
```json
{
  "success": true,
  "message": "OK",
  "data": { "id": 1, "title": "Nơi Này Có Anh", "artistId": 5 }
}
```

**Response mẫu — lỗi:**
```json
{
  "success": false,
  "message": "Song not found with id: 99",
  "data": null
}
```

---

## 8. XỬ LÝ EXCEPTION

- Tất cả exception xử lý **tập trung** tại `GlobalExceptionHandler.java`.
- **KHÔNG** xử lý exception bằng try-catch trong Controller.
- Tạo custom exception cho từng loại lỗi nghiệp vụ.

```java
// presentation/advice/GlobalExceptionHandler.java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(DuplicateSongException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicate(DuplicateSongException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(message));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorized(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ex.getMessage()));
    }
}
```

---

## 9. VALIDATION

- Validate input ở **DTO layer** bằng Bean Validation (`jakarta.validation`).
- Validate business rule ở **Service layer**.
- **KHÔNG** validate ở Controller trực tiếp — dùng `@Valid` và để `GlobalExceptionHandler` xử lý.

```java
// application/dto/request/CreateSongDto.java
@Data
public class CreateSongDto {
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @NotNull(message = "Artist ID is required")
    private Long artistId;

    private Long albumId;  // optional — null nếu là single

    @NotNull(message = "Genre ID is required")
    private Long genreId;

    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be positive")
    private Integer durationSeconds;
}
```

---

## 10. BẢO MẬT (SECURITY)

- Authentication bằng **JWT** — token đính kèm trong header `Authorization: Bearer <token>`.
- Authorization bằng **Spring Security** với Role-based access control:

| Role    | Quyền                                    |
|---------|------------------------------------------|
| `USER`  | Nghe nhạc, quản lý playlist, yêu thích  |
| `ADMIN` | CRUD bài hát, quản lý user, xem thống kê |

- Endpoint công khai (không cần auth): `POST /api/auth/login`, `POST /api/auth/register`
- Endpoint yêu cầu ADMIN: Mọi `POST`, `PUT`, `DELETE` của `/api/admin/**`
- **Không bao giờ** log ra password hay JWT secret.
- Message lỗi auth dùng chung `"Invalid credentials"` — **không tiết lộ** email nào tồn tại.

---

## 11. QUẢN LÝ DATABASE SCHEMA

Schema được quản lý **hoàn toàn qua JPA Model** — Hibernate tự đồng bộ dựa trên `ddl-auto=update`.

- **Không dùng Flyway** hay công cụ migration thủ công.
- Mọi thay đổi schema (thêm bảng, thêm cột, đổi kiểu dữ liệu) thực hiện bằng cách **chỉnh sửa JPA Model** tương ứng.
- Cấu hình trong `application.yml`:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update      # Tự update schema khi khởi động
    show-sql: true          # Bật khi dev để kiểm tra câu SQL sinh ra
    properties:
      hibernate:
        format_sql: true
```

> ⚠️ Khi thêm cột mới vào Model, đảm bảo cột đó có giá trị **`nullable = true`** hoặc có `columnDefinition` với `DEFAULT` để tránh lỗi khi update bảng đã có dữ liệu.

---


## 12. QUY TẮC KIỂM THỬ

> ⚠️ **Không được merge** branch feature vào `dev` nếu chưa có unit test cho Service tương ứng.

### 12.1 Phạm vi test

| Layer | Loại test | Mức độ | Lý do |
|-------|-----------|--------|-------|
| **Service** | Unit test (mock RepoPort) | ✅ **Bắt buộc** | Chứa toàn bộ business logic |
| **Controller** | Integration test (`@WebMvcTest`) | ⚡ **Nên có** | Kiểm tra HTTP status, request/response format |
| **Adapter / JpaRepo** | — | ❌ **Không cần** | Không có business logic, Spring Data JPA đã đảm bảo |

### 12.2 Vị trí đặt test

```
src/test/java/com/example/ondas/
├── unit/
│   └── service/
│       ├── AuthServiceTest.java
│       ├── SongServiceTest.java
│       └── ...
└── integration/
    └── controller/
        ├── AuthControllerTest.java
        ├── SongControllerTest.java
        └── ...
```

### 12.3 Unit Test — Service (Bắt buộc)

**Yêu cầu:** Mọi public method của Service phải có test. Dùng **JUnit 5 + Mockito**, mock toàn bộ `*RepoPort`.

```java
// unit/service/SongServiceTest.java
@ExtendWith(MockitoExtension.class)
class SongServiceTest {

    @Mock
    private SongRepoPort songRepoPort;

    @Mock
    private ArtistRepoPort artistRepoPort;

    @InjectMocks
    private SongService songService;

    @Test
    void createSong_WhenValid_ShouldReturnSongDto() {
        // arrange
        CreateSongDto request = new CreateSongDto();
        request.setTitle("Nơi Này Có Anh");
        request.setArtistId(1L);
        request.setDurationSeconds(210);

        when(artistRepoPort.existsById(1L)).thenReturn(true);
        when(songRepoPort.existsByTitleAndArtistId(any(), any())).thenReturn(false);
        when(songRepoPort.save(any())).thenReturn(
            new Song(1L, "Nơi Này Có Anh", 1L, null, "url", 210));

        // act
        SongDto result = songService.createSong(request);

        // assert
        assertNotNull(result);
        assertEquals("Nơi Này Có Anh", result.getTitle());
        verify(songRepoPort).save(any());
    }

    @Test
    void createSong_WhenArtistNotFound_ShouldThrowNotFoundException() {
        CreateSongDto request = new CreateSongDto();
        request.setArtistId(99L);
        when(artistRepoPort.existsById(99L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> songService.createSong(request));
        verify(songRepoPort, never()).save(any());
    }

    @Test
    void createSong_WhenDuplicateTitle_ShouldThrowDuplicateSongException() {
        CreateSongDto request = new CreateSongDto();
        request.setTitle("Nơi Này Có Anh");
        request.setArtistId(1L);
        when(artistRepoPort.existsById(1L)).thenReturn(true);
        when(songRepoPort.existsByTitleAndArtistId("Nơi Này Có Anh", 1L)).thenReturn(true);

        assertThrows(DuplicateSongException.class, () -> songService.createSong(request));
    }
}
```

### 12.4 Integration Test — Controller (Nên có)

Dùng `@WebMvcTest` để test HTTP layer — không khởi động full context, mock Service:

```java
// integration/controller/SongControllerTest.java
@WebMvcTest(SongController.class)
class SongControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SongServicePort songServicePort;

    @Test
    void createSong_ShouldReturn201_WhenRequestValid() throws Exception {
        SongDto mockResult = new SongDto(1L, "Nơi Này Có Anh", 1L, 210);
        when(songServicePort.createSong(any())).thenReturn(mockResult);

        mockMvc.perform(post("/api/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "title": "Nơi Này Có Anh",
                        "artistId": 1,
                        "genreId": 2,
                        "durationSeconds": 210
                    }
                """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.title").value("Nơi Này Có Anh"));
    }

    @Test
    void createSong_ShouldReturn400_WhenTitleMissing() throws Exception {
        mockMvc.perform(post("/api/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{ "artistId": 1, "durationSeconds": 210 }"""))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false));
    }
}
```

### 12.5 Chạy test

```bash
# Chạy toàn bộ test
./mvnw test

# Chạy test 1 class cụ thể
./mvnw test -Dtest=SongServiceTest

# Chạy với coverage report (JaCoCo)
./mvnw test jacoco:report
```

**Yêu cầu coverage:** Service layer ≥ 70%.

---


## 13. GIT WORKFLOW

### Branch strategy

```
main          ← Production release
└── dev       ← Integration (merge cuối mỗi tuần)
    ├── feature/trieu-<tên-task>
    ├── feature/thanh-<tên-task>
    └── feature/bac-<tên-task>
```

### Commit message format

```
feat(auth): thêm endpoint đăng nhập bằng JWT
feat(song): tạo CRUD API bài hát
fix(security): sửa lỗi token không refresh được
test(song): thêm unit test cho SongService.createSong
refactor(adapter): tách converter logic sang SongModel
```

---

## 14. QUY TẮC VIẾT CODE CHUNG

1. **Không hardcode** URL, secret key, database config — đặt trong `application.yml` và dùng `@Value` hoặc `@ConfigurationProperties`.
2. **Không inject** `*JpaRepo` vào Service — luôn inject `*RepoPort`.
3. **Không để** business logic trong Controller hay Adapter.
4. **Luôn dùng** `@Valid` cho request body trong Controller.
5. **Comment** bằng tiếng Việt cho business logic phức tạp, JavaDoc bằng tiếng Anh cho public API.
6. **Không log** thông tin nhạy cảm (password, token, thông tin cá nhân).
7. Mỗi Service implementation **chỉ xử lý nghiệp vụ của 1 entity chính** — nếu cần nhiều entity, inject thêm Port tương ứng.

---

## 15. BUILD & RUN COMMANDS

```bash
# Chạy development
./mvnw spring-boot:run

# Build jar
./mvnw clean package -DskipTests

# Chạy toàn bộ test
./mvnw test

# Chạy với profile cụ thể
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

---

## 16. CHECKLIST TRƯỚC KHI COMMIT

### Kiến trúc & Code
- [ ] Code đặt đúng tầng (domain / application / infrastructure / presentation)
- [ ] Domain Entity KHÔNG có annotation Spring/JPA/Lombok
- [ ] Service inject `*RepoPort`, KHÔNG inject `*JpaRepo` trực tiếp
- [ ] Controller inject `*ServicePort`, KHÔNG inject Service impl trực tiếp
- [ ] Adapter implements RepoPort từ domain
- [ ] JPA Model có đủ `toDomain()` và `fromDomain()`
- [ ] Mapper: map inline (constructor/Builder) nếu chỉ dùng 1 lần, tách class `XxxMapper` nếu dùng ≥ 2 nơi

### API & Validation
- [ ] Mọi API trả về `ResponseEntity<ApiResponse<T>>`
- [ ] Request DTO có đầy đủ Bean Validation annotation
- [ ] Exception xử lý qua `GlobalExceptionHandler`, không try-catch ở Controller
- [ ] API path đúng convention REST và `kebab-case`

### Kiểm thử
- [ ] Có unit test cho Service method (happy path + error + edge case)
- [ ] Test dùng Mock cho `*RepoPort`, không gọi DB thật
- [ ] `./mvnw test` pass 100%, không có lỗi

### Database
- [ ] Thay đổi schema thực hiện bằng cách sửa JPA Model (không dùng Flyway)
- [ ] Cột mới thêm vào Model phải có `nullable = true` hoặc giá trị DEFAULT nếu bảng đã có data

### Bảo mật
- [ ] Endpoint mới có phân quyền đúng (USER / ADMIN / PUBLIC)
- [ ] Không log password, token hoặc thông tin nhạy cảm
- [ ] Commit message theo đúng format `feat/fix/test/refactor(scope): ...`
