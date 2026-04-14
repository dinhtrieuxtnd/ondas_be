package com.example.ondas_be.presentation.controller;

import com.example.ondas_be.application.dto.request.CreateSongDto;
import com.example.ondas_be.application.dto.request.UpdateSongDto;
import com.example.ondas_be.application.dto.response.SongDto;
import com.example.ondas_be.application.service.port.SongServicePort;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/songs")
public class SongController {

    private final SongServicePort songServicePort;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SongDto create(
        HttpServletRequest request,
        @RequestPart("audio") MultipartFile audioFile,
        @RequestPart(value = "cover", required = false) MultipartFile coverFile
    ) throws Exception {
        Part dataPart = request.getPart("data");
        String dataJson = new String(dataPart.getInputStream().readAllBytes());
        CreateSongDto dto = objectMapper.readValue(dataJson, CreateSongDto.class);
        return songServicePort.create(dto, audioFile, coverFile);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SongDto update(
        @PathVariable UUID id,
        HttpServletRequest request,
        @RequestPart(value = "audio", required = false) MultipartFile audioFile,
        @RequestPart(value = "cover", required = false) MultipartFile coverFile
    ) throws Exception {
        Part dataPart = request.getPart("data");
        String dataJson = new String(dataPart.getInputStream().readAllBytes());
        UpdateSongDto dto = objectMapper.readValue(dataJson, UpdateSongDto.class);
        return songServicePort.update(id, dto, audioFile, coverFile);
    }

    @GetMapping("/{id}")
    public SongDto getById(@PathVariable UUID id) {
        return songServicePort.getById(id);
    }

    @GetMapping
    public List<SongDto> getAll() {
        return songServicePort.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        songServicePort.delete(id);
    }

    // ============ FILTER + PAGINATION ENDPOINTS ============

    /**
     * Search songs by keyword, album, genre with pagination
     * Query params:
     * - keyword: search by title/slug (optional)
     * - albumId: filter by album UUID (optional)
     * - genreId: filter by genre ID (optional)
     * - page: 0-based page number (default: 0)
     * - size: page size (default: 20)
     * - sort: sort by field, e.g., "createdAt,desc" (default: "createdAt,desc")
     */
    @GetMapping("/search")
    public Page<SongDto> search(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) UUID albumId,
        @RequestParam(required = false) Integer genreId,
        @PageableDefault(size = 20, page = 0, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return songServicePort.findByFilters(keyword, albumId, genreId, pageable);
    }

    /**
     * Get songs by album with pagination
     */
    @GetMapping("/album/{albumId}")
    public Page<SongDto> getByAlbum(
        @PathVariable UUID albumId,
        @PageableDefault(size = 20, page = 0, sort = "trackNumber", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return songServicePort.findByAlbumId(albumId, pageable);
    }

    /**
     * Get songs by genre with pagination
     */
    @GetMapping("/genre/{genreId}")
    public Page<SongDto> getByGenre(
        @PathVariable Integer genreId,
        @PageableDefault(size = 20, page = 0, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return songServicePort.findByGenreId(genreId, pageable);
    }

    /**
     * Get all active songs with pagination
     */
    @GetMapping("/active")
    public Page<SongDto> getActive(
        @PageableDefault(size = 20, page = 0, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return songServicePort.findAllActive(pageable);
    }
}

