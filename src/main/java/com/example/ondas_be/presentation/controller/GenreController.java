package com.example.ondas_be.presentation.controller;

import com.example.ondas_be.application.dto.common.ApiResponse;
import com.example.ondas_be.application.dto.common.PageResultDto;
import com.example.ondas_be.application.dto.request.CreateGenreRequest;
import com.example.ondas_be.application.dto.request.UpdateGenreRequest;
import com.example.ondas_be.application.dto.response.GenreResponse;
import com.example.ondas_be.application.service.port.GenreServicePort;
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

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreServicePort genreServicePort;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','CONTENT_MANAGER')")
    public ResponseEntity<ApiResponse<GenreResponse>> createGenre(
            @Valid @RequestPart("data") CreateGenreRequest request,
            @RequestPart(value = "cover", required = false) MultipartFile coverFile) {
        GenreResponse response = genreServicePort.createGenre(request, coverFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','CONTENT_MANAGER')")
    public ResponseEntity<ApiResponse<GenreResponse>> updateGenre(
            @PathVariable Long id,
            @RequestPart("data") UpdateGenreRequest request,
            @RequestPart(value = "cover", required = false) MultipartFile coverFile) {
        GenreResponse response = genreServicePort.updateGenre(id, request, coverFile);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GenreResponse>> getGenreById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(genreServicePort.getGenreById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GenreResponse>>> getAllGenres() {
        return ResponseEntity.ok(ApiResponse.success(genreServicePort.getAllGenres()));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResultDto<GenreResponse>>> searchGenresByName(
            @RequestParam String query,
            @RequestParam(defaultValue = "contains") String mode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(genreServicePort.searchGenresByName(query, mode, page, size)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CONTENT_MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteGenre(@PathVariable Long id) {
        genreServicePort.deleteGenre(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
