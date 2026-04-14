package com.example.ondas_be.application.service.impl;

import com.example.ondas_be.application.dto.request.AlbumTrackItemDto;
import com.example.ondas_be.application.dto.request.CreateAlbumDto;
import com.example.ondas_be.application.dto.request.UpdateAlbumDto;
import com.example.ondas_be.application.dto.response.AlbumDto;
import com.example.ondas_be.application.service.port.AlbumServicePort;
import com.example.ondas_be.domain.entity.Album;
import com.example.ondas_be.domain.repoport.AlbumRepoPort;
import com.example.ondas_be.domain.repoport.FileStoragePort;
import com.example.ondas_be.domain.repoport.SongRepoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlbumService implements AlbumServicePort {

    private final AlbumRepoPort albumRepoPort;
    private final SongRepoPort songRepoPort;
    private final FileStoragePort fileStoragePort;

    @Override
    public AlbumDto create(CreateAlbumDto dto, MultipartFile coverFile) {
        String coverUrl = null;
        if (coverFile != null && !coverFile.isEmpty()) {
            coverUrl = uploadFile("albums/cover", coverFile).url();
        }

        Album album = new Album(
            null,
            dto.getTitle(),
            dto.getSlug(),
            coverUrl,
            dto.getReleaseDate(),
            dto.getAlbumType() != null ? dto.getAlbumType() : "album",
            dto.getDescription(),
            0,
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        Album saved = albumRepoPort.save(album);
        updateTracklist(saved.getId(), dto.getTracks());

        Album latest = albumRepoPort.findById(saved.getId()).orElse(saved);
        return toDto(latest);
    }

    @Override
    public AlbumDto update(UUID id, UpdateAlbumDto dto, MultipartFile coverFile) {
        Album existing = albumRepoPort.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("album not found"));

        String coverUrl = existing.getCoverUrl();
        if (coverFile != null && !coverFile.isEmpty()) {
            coverUrl = uploadFile("albums/cover", coverFile).url();
        }

        Album updated = new Album(
            existing.getId(),
            dto.getTitle() != null ? dto.getTitle() : existing.getTitle(),
            dto.getSlug() != null ? dto.getSlug() : existing.getSlug(),
            coverUrl,
            dto.getReleaseDate() != null ? dto.getReleaseDate() : existing.getReleaseDate(),
            dto.getAlbumType() != null ? dto.getAlbumType() : existing.getAlbumType(),
            dto.getDescription() != null ? dto.getDescription() : existing.getDescription(),
            existing.getTotalTracks(),
            existing.getCreatedAt(),
            LocalDateTime.now()
        );

        Album saved = albumRepoPort.save(updated);
        if (dto.getTracks() != null) {
            updateTracklist(saved.getId(), dto.getTracks());
        }

        Album latest = albumRepoPort.findById(saved.getId()).orElse(saved);
        return toDto(latest);
    }

    @Override
    public AlbumDto getById(UUID id) {
        Album album = albumRepoPort.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("album not found"));
        return toDto(album);
    }

    @Override
    public List<AlbumDto> getAll() {
        return albumRepoPort.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public void delete(UUID id) {
        if (!albumRepoPort.existsById(id)) {
            throw new IllegalArgumentException("album not found");
        }
        albumRepoPort.deleteById(id);
    }

    private void updateTracklist(UUID albumId, List<AlbumTrackItemDto> tracks) {
        if (tracks == null || tracks.isEmpty()) {
            return;
        }
        List<AlbumTrackItemDto> sorted = tracks.stream()
            .sorted(Comparator.comparing(item -> item.getTrackNumber() != null ? item.getTrackNumber() : Integer.MAX_VALUE))
            .toList();

        for (int i = 0; i < sorted.size(); i++) {
            AlbumTrackItemDto item = sorted.get(i);
            int trackNumber = item.getTrackNumber() != null ? item.getTrackNumber() : (i + 1);
            songRepoPort.updateAlbumTrack(item.getSongId(), albumId, trackNumber);
        }
    }

    private FileStoragePort.StoredFile uploadFile(String folder, MultipartFile file) {
        try {
            return fileStoragePort.upload(
                folder,
                file.getOriginalFilename(),
                file.getInputStream(),
                file.getSize(),
                file.getContentType()
            );
        } catch (IOException e) {
            throw new IllegalStateException("upload failed", e);
        }
    }

    private AlbumDto toDto(Album album) {
        AlbumDto dto = new AlbumDto();
        dto.setId(album.getId());
        dto.setTitle(album.getTitle());
        dto.setSlug(album.getSlug());
        dto.setCoverUrl(album.getCoverUrl());
        dto.setReleaseDate(album.getReleaseDate());
        dto.setAlbumType(album.getAlbumType());
        dto.setDescription(album.getDescription());
        dto.setTotalTracks(album.getTotalTracks());
        dto.setCreatedAt(album.getCreatedAt());
        dto.setUpdatedAt(album.getUpdatedAt());
        return dto;
    }
}
