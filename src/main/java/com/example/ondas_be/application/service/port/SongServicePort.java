package com.example.ondas_be.application.service.port;

import com.example.ondas_be.application.dto.request.CreateSongDto;
import com.example.ondas_be.application.dto.request.UpdateSongDto;
import com.example.ondas_be.application.dto.response.SongDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface SongServicePort {

    SongDto create(CreateSongDto dto, MultipartFile audioFile, MultipartFile coverFile);

    SongDto update(UUID id, UpdateSongDto dto, MultipartFile audioFile, MultipartFile coverFile);

    SongDto getById(UUID id);

    List<SongDto> getAll();

    void delete(UUID id);

    // New filter + pagination methods
    Page<SongDto> findByAlbumId(UUID albumId, Pageable pageable);

    Page<SongDto> findByGenreId(Integer genreId, Pageable pageable);

    Page<SongDto> findAllActive(Pageable pageable);

    Page<SongDto> findByKeyword(String keyword, Pageable pageable);

    Page<SongDto> findByFilters(String keyword, UUID albumId, Integer genreId, Pageable pageable);
}

