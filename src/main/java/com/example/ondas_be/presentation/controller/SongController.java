package com.example.ondas_be.presentation.controller;

import com.example.ondas_be.application.dto.common.ApiResponse;
import com.example.ondas_be.application.dto.common.PageResultDto;
import com.example.ondas_be.application.dto.request.CreateSongRequest;
import com.example.ondas_be.application.dto.request.UpdateSongRequest;
import com.example.ondas_be.application.dto.response.SongResponse;
import com.example.ondas_be.application.service.port.SongServicePort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/songs")
@RequiredArgsConstructor
public class SongController {

    private final SongServicePort songServicePort;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','CONTENT_MANAGER')")
    public ResponseEntity<ApiResponse<SongResponse>> createSong(
            @Valid @RequestPart("data") CreateSongRequest request,
            @RequestPart("audio") MultipartFile audioFile,
            @RequestPart(value = "cover", required = false) MultipartFile coverFile) {
        SongResponse response = songServicePort.createSong(request, audioFile, coverFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','CONTENT_MANAGER')")
    public ResponseEntity<ApiResponse<SongResponse>> updateSong(
            @PathVariable UUID id,
            @RequestPart("data") UpdateSongRequest request,
            @RequestPart(value = "audio", required = false) MultipartFile audioFile,
            @RequestPart(value = "cover", required = false) MultipartFile coverFile) {
        SongResponse response = songServicePort.updateSong(id, request, audioFile, coverFile);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SongResponse>> getSongById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(songServicePort.getSongById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SongResponse>>> getAllSongs() {
        return ResponseEntity.ok(ApiResponse.success(songServicePort.getAllSongs()));
    }

    @GetMapping("/by-artist/{artistId}")
    public ResponseEntity<ApiResponse<PageResultDto<SongResponse>>> getSongsByArtist(
            @PathVariable UUID artistId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(songServicePort.getSongsByArtist(artistId, page, size)));
    }

    @GetMapping("/by-album/{albumId}")
    public ResponseEntity<ApiResponse<PageResultDto<SongResponse>>> getSongsByAlbum(
            @PathVariable UUID albumId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(songServicePort.getSongsByAlbum(albumId, page, size)));
    }

    @GetMapping("/by-genre/{genreId}")
    public ResponseEntity<ApiResponse<PageResultDto<SongResponse>>> getSongsByGenre(
            @PathVariable Long genreId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(songServicePort.getSongsByGenre(genreId, page, size)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResultDto<SongResponse>>> searchSongsByTitle(
            @RequestParam String query,
            @RequestParam(defaultValue = "contains") String mode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(songServicePort.searchSongsByTitle(query, mode, page, size)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CONTENT_MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteSong(@PathVariable UUID id) {
        songServicePort.deleteSong(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
