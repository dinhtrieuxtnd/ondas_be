package com.example.ondas_be.application.service.impl;

import com.example.ondas_be.application.dto.request.CreateSongDto;
import com.example.ondas_be.application.dto.request.UpdateSongDto;
import com.example.ondas_be.application.dto.response.SongDto;
import com.example.ondas_be.application.service.port.SongServicePort;
import com.example.ondas_be.domain.entity.Song;
import com.example.ondas_be.domain.repoport.FileStoragePort;
import com.example.ondas_be.domain.repoport.GenreRepoPort;
import com.example.ondas_be.domain.repoport.SongRepoPort;
import com.example.ondas_be.domain.repoport.AlbumRepoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SongService implements SongServicePort {

    private final SongRepoPort songRepoPort;
    private final GenreRepoPort genreRepoPort;
    private final AlbumRepoPort albumRepoPort;
    private final FileStoragePort fileStoragePort;

    @Override
    public SongDto create(CreateSongDto dto, MultipartFile audioFile, MultipartFile coverFile) {
        if (audioFile == null || audioFile.isEmpty()) {
            throw new IllegalArgumentException("audioFile is required");
        }
        validateGenres(dto.getGenreIds());
        if (dto.getAlbumId() != null && !albumRepoPort.existsById(dto.getAlbumId())) {
            throw new IllegalArgumentException("albumId not found");
        }

        FileStoragePort.StoredFile audioStored = uploadFile("songs/audio", audioFile);
        FileStoragePort.StoredFile coverStored = coverFile != null && !coverFile.isEmpty()
            ? uploadFile("songs/cover", coverFile)
            : null;

        Song song = new Song(
            null,
            dto.getTitle(),
            dto.getSlug(),
            dto.getDurationSeconds(),
            audioStored.url(),
            dto.getAudioFormat() != null ? dto.getAudioFormat() : inferAudioFormat(audioFile.getOriginalFilename()),
            audioFile.getSize(),
            coverStored != null ? coverStored.url() : null,
            dto.getAlbumId(),
            dto.getTrackNumber(),
            dto.getReleaseDate(),
            0L,
            dto.getActive() != null ? dto.getActive() : true,
            toGenreSet(dto.getGenreIds()),
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        Song saved = songRepoPort.save(song);
        return toDto(saved, dto.getGenreIds());
    }

    @Override
    public SongDto update(UUID id, UpdateSongDto dto, MultipartFile audioFile, MultipartFile coverFile) {
        Song existing = songRepoPort.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("song not found"));

        if (dto.getGenreIds() != null) {
            validateGenres(dto.getGenreIds());
        }
        if (dto.getAlbumId() != null && !albumRepoPort.existsById(dto.getAlbumId())) {
            throw new IllegalArgumentException("albumId not found");
        }

        String audioUrl = existing.getAudioUrl();
        String audioFormat = existing.getAudioFormat();
        Long audioSizeBytes = existing.getAudioSizeBytes();
        if (audioFile != null && !audioFile.isEmpty()) {
            FileStoragePort.StoredFile audioStored = uploadFile("songs/audio", audioFile);
            audioUrl = audioStored.url();
            audioFormat = dto.getAudioFormat() != null ? dto.getAudioFormat() : inferAudioFormat(audioFile.getOriginalFilename());
            audioSizeBytes = audioFile.getSize();
        }

        String coverUrl = existing.getCoverUrl();
        if (coverFile != null && !coverFile.isEmpty()) {
            FileStoragePort.StoredFile coverStored = uploadFile("songs/cover", coverFile);
            coverUrl = coverStored.url();
        }

        Song updated = new Song(
            existing.getId(),
            dto.getTitle() != null ? dto.getTitle() : existing.getTitle(),
            dto.getSlug() != null ? dto.getSlug() : existing.getSlug(),
            dto.getDurationSeconds() != null ? dto.getDurationSeconds() : existing.getDurationSeconds(),
            audioUrl,
            audioFormat,
            audioSizeBytes,
            coverUrl,
            dto.getAlbumId() != null ? dto.getAlbumId() : existing.getAlbumId(),
            dto.getTrackNumber() != null ? dto.getTrackNumber() : existing.getTrackNumber(),
            dto.getReleaseDate() != null ? dto.getReleaseDate() : existing.getReleaseDate(),
            existing.getPlayCount(),
            dto.getActive() != null ? dto.getActive() : existing.isActive(),
            dto.getGenreIds() != null ? toGenreSet(dto.getGenreIds()) : existing.getGenreIds(),
            existing.getCreatedAt(),
            LocalDateTime.now()
        );

        Song saved = songRepoPort.save(updated);
        return toDto(saved, dto.getGenreIds() != null ? dto.getGenreIds() : toGenreList(existing.getGenreIds()));
    }

    @Override
    public SongDto getById(UUID id) {
        Song song = songRepoPort.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("song not found"));
        return toDto(song, toGenreList(song.getGenreIds()));
    }

    @Override
    public List<SongDto> getAll() {
        return songRepoPort.findAll().stream()
            .map(song -> toDto(song, toGenreList(song.getGenreIds())))
            .toList();
    }

    @Override
    public void delete(UUID id) {
        if (!songRepoPort.existsById(id)) {
            throw new IllegalArgumentException("song not found");
        }
        songRepoPort.deleteById(id);
    }

    @Override
    public Page<SongDto> findByAlbumId(UUID albumId, Pageable pageable) {
        return songRepoPort.findActiveByAlbumId(albumId, pageable)
            .map(song -> toDto(song, toGenreList(song.getGenreIds())));
    }

    @Override
    public Page<SongDto> findByGenreId(Integer genreId, Pageable pageable) {
        return songRepoPort.findActiveByGenreId(genreId, pageable)
            .map(song -> toDto(song, toGenreList(song.getGenreIds())));
    }

    @Override
    public Page<SongDto> findAllActive(Pageable pageable) {
        return songRepoPort.findAllActive(pageable)
            .map(song -> toDto(song, toGenreList(song.getGenreIds())));
    }

    @Override
    public Page<SongDto> findByKeyword(String keyword, Pageable pageable) {
        return songRepoPort.findByKeyword(keyword, pageable)
            .map(song -> toDto(song, toGenreList(song.getGenreIds())));
    }

    @Override
    public Page<SongDto> findByFilters(String keyword, UUID albumId, Integer genreId, Pageable pageable) {
        // If specific filters provided, use them
        if (genreId != null) {
            return findByGenreId(genreId, pageable);
        }
        if (albumId != null) {
            return findByAlbumId(albumId, pageable);
        }
        if (keyword != null && !keyword.isBlank()) {
            return findByKeyword(keyword, pageable);
        }
        // Default: return all active
        return findAllActive(pageable);
    }

    private void validateGenres(List<Integer> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) {
            return;
        }
        List<Integer> existing = genreRepoPort.findAllById(genreIds).stream()
            .map(g -> g.getId())
            .toList();
        if (existing.size() != genreIds.size()) {
            throw new IllegalArgumentException("genreIds not found");
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

    private String inferAudioFormat(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "mp3";
        }
        String ext = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        return ext.isEmpty() ? "mp3" : ext;
    }

    private Set<Integer> toGenreSet(List<Integer> ids) {
        return ids == null ? Collections.emptySet() : new HashSet<>(ids);
    }

    private List<Integer> toGenreList(Set<Integer> ids) {
        return ids == null ? Collections.emptyList() : ids.stream().toList();
    }

    private SongDto toDto(Song song, List<Integer> genreIds) {
        SongDto dto = new SongDto();
        dto.setId(song.getId());
        dto.setTitle(song.getTitle());
        dto.setSlug(song.getSlug());
        dto.setDurationSeconds(song.getDurationSeconds());
        dto.setAudioUrl(song.getAudioUrl());
        dto.setAudioFormat(song.getAudioFormat());
        dto.setAudioSizeBytes(song.getAudioSizeBytes());
        dto.setCoverUrl(song.getCoverUrl());
        dto.setAlbumId(song.getAlbumId());
        dto.setTrackNumber(song.getTrackNumber());
        dto.setReleaseDate(song.getReleaseDate());
        dto.setPlayCount(song.getPlayCount());
        dto.setActive(song.isActive());
        dto.setGenreIds(genreIds != null ? genreIds : List.of());
        dto.setCreatedAt(song.getCreatedAt());
        dto.setUpdatedAt(song.getUpdatedAt());
        return dto;
    }
}
