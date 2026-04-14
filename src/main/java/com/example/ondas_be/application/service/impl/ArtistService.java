package com.example.ondas_be.application.service.impl;

import com.example.ondas_be.application.dto.request.CreateArtistDto;
import com.example.ondas_be.application.dto.request.UpdateArtistDto;
import com.example.ondas_be.application.dto.response.ArtistDto;
import com.example.ondas_be.application.service.port.ArtistServicePort;
import com.example.ondas_be.domain.entity.Artist;
import com.example.ondas_be.domain.repoport.ArtistRepoPort;
import com.example.ondas_be.domain.repoport.FileStoragePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArtistService implements ArtistServicePort {

    private final ArtistRepoPort artistRepoPort;
    private final FileStoragePort fileStoragePort;

    @Override
    public ArtistDto create(CreateArtistDto dto, MultipartFile avatarFile) {
        String avatarUrl = null;
        if (avatarFile != null && !avatarFile.isEmpty()) {
            avatarUrl = uploadFile("artists/avatar", avatarFile).url();
        }

        Artist artist = new Artist(
            null,
            dto.getName(),
            dto.getSlug(),
            dto.getBio(),
            avatarUrl,
            dto.getCountry(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        Artist saved = artistRepoPort.save(artist);
        return toDto(saved);
    }

    @Override
    public ArtistDto update(UUID id, UpdateArtistDto dto, MultipartFile avatarFile) {
        Artist existing = artistRepoPort.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("artist not found"));

        String avatarUrl = existing.getAvatarUrl();
        if (avatarFile != null && !avatarFile.isEmpty()) {
            avatarUrl = uploadFile("artists/avatar", avatarFile).url();
        }

        Artist updated = new Artist(
            existing.getId(),
            dto.getName() != null ? dto.getName() : existing.getName(),
            dto.getSlug() != null ? dto.getSlug() : existing.getSlug(),
            dto.getBio() != null ? dto.getBio() : existing.getBio(),
            avatarUrl,
            dto.getCountry() != null ? dto.getCountry() : existing.getCountry(),
            existing.getCreatedAt(),
            LocalDateTime.now()
        );

        Artist saved = artistRepoPort.save(updated);
        return toDto(saved);
    }

    @Override
    public ArtistDto getById(UUID id) {
        Artist artist = artistRepoPort.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("artist not found"));
        return toDto(artist);
    }

    @Override
    public List<ArtistDto> getAll() {
        return artistRepoPort.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public void delete(UUID id) {
        if (!artistRepoPort.existsById(id)) {
            throw new IllegalArgumentException("artist not found");
        }
        artistRepoPort.deleteById(id);
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

    private ArtistDto toDto(Artist artist) {
        ArtistDto dto = new ArtistDto();
        dto.setId(artist.getId());
        dto.setName(artist.getName());
        dto.setSlug(artist.getSlug());
        dto.setBio(artist.getBio());
        dto.setAvatarUrl(artist.getAvatarUrl());
        dto.setCountry(artist.getCountry());
        dto.setCreatedAt(artist.getCreatedAt());
        dto.setUpdatedAt(artist.getUpdatedAt());
        return dto;
    }
}
