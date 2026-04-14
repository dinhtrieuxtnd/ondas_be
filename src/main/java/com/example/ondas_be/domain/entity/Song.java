package com.example.ondas_be.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

// Pure domain entity — no Spring/JPA/Lombok
public class Song {

    private final UUID id;
    private final String title;
    private final String slug;
    private final int durationSeconds;
    private final String audioUrl;
    private final String audioFormat;
    private final Long audioSizeBytes;
    private final String coverUrl;
    private final UUID albumId;
    private final Integer trackNumber;
    private final LocalDate releaseDate;
    private final long playCount;
    private final boolean active;
    private final Set<Integer> genreIds;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public Song(UUID id, String title, String slug, int durationSeconds, String audioUrl,
                String audioFormat, Long audioSizeBytes, String coverUrl, UUID albumId,
                Integer trackNumber, LocalDate releaseDate, long playCount, boolean active,
                Set<Integer> genreIds, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.durationSeconds = durationSeconds;
        this.audioUrl = audioUrl;
        this.audioFormat = audioFormat;
        this.audioSizeBytes = audioSizeBytes;
        this.coverUrl = coverUrl;
        this.albumId = albumId;
        this.trackNumber = trackNumber;
        this.releaseDate = releaseDate;
        this.playCount = playCount;
        this.active = active;
        this.genreIds = genreIds;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getSlug() { return slug; }
    public int getDurationSeconds() { return durationSeconds; }
    public String getAudioUrl() { return audioUrl; }
    public String getAudioFormat() { return audioFormat; }
    public Long getAudioSizeBytes() { return audioSizeBytes; }
    public String getCoverUrl() { return coverUrl; }
    public UUID getAlbumId() { return albumId; }
    public Integer getTrackNumber() { return trackNumber; }
    public LocalDate getReleaseDate() { return releaseDate; }
    public long getPlayCount() { return playCount; }
    public boolean isActive() { return active; }
    public Set<Integer> getGenreIds() { return genreIds; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
