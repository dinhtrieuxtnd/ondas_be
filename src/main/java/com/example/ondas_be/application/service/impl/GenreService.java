package com.example.ondas_be.application.service.impl;

import com.example.ondas_be.application.dto.request.CreateGenreDto;
import com.example.ondas_be.application.dto.request.UpdateGenreDto;
import com.example.ondas_be.application.dto.response.GenreDto;
import com.example.ondas_be.application.service.port.GenreServicePort;
import com.example.ondas_be.domain.entity.Genre;
import com.example.ondas_be.domain.repoport.GenreRepoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService implements GenreServicePort {

    private final GenreRepoPort genreRepoPort;

    @Override
    public GenreDto create(CreateGenreDto dto) {
        Genre genre = new Genre(null, dto.getName(), dto.getSlug(), dto.getDescription(),
            dto.getCoverUrl(), LocalDateTime.now());

        Genre saved = genreRepoPort.save(genre);
        return toDto(saved);
    }

    @Override
    public GenreDto update(Integer id, UpdateGenreDto dto) {
        Genre existing = genreRepoPort.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("genre not found"));

        Genre updated = new Genre(
            existing.getId(),
            dto.getName() != null ? dto.getName() : existing.getName(),
            dto.getSlug() != null ? dto.getSlug() : existing.getSlug(),
            dto.getDescription() != null ? dto.getDescription() : existing.getDescription(),
            dto.getCoverUrl() != null ? dto.getCoverUrl() : existing.getCoverUrl(),
            existing.getCreatedAt()
        );

        Genre saved = genreRepoPort.save(updated);
        return toDto(saved);
    }

    @Override
    public GenreDto getById(Integer id) {
        Genre genre = genreRepoPort.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("genre not found"));
        return toDto(genre);
    }

    @Override
    public List<GenreDto> getAll() {
        return genreRepoPort.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public void delete(Integer id) {
        if (!genreRepoPort.existsById(id)) {
            throw new IllegalArgumentException("genre not found");
        }
        genreRepoPort.deleteById(id);
    }

    private GenreDto toDto(Genre genre) {
        GenreDto dto = new GenreDto();
        dto.setId(genre.getId());
        dto.setName(genre.getName());
        dto.setSlug(genre.getSlug());
        dto.setDescription(genre.getDescription());
        dto.setCoverUrl(genre.getCoverUrl());
        dto.setCreatedAt(genre.getCreatedAt());
        return dto;
    }
}
