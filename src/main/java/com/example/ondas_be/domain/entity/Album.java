package com.example.ondas_be.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

// Pure domain entity — no Spring/JPA/Lombok
public class Album {

    private final UUID id;
    private final String title;
    private final String slug;
    private final String coverUrl;
    private final LocalDate releaseDate;
    private final String albumType;
    private final String description;
    private final int totalTracks;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public Album(UUID id, String title, String slug, String coverUrl,
                 LocalDate releaseDate, String albumType, String description,
                 int totalTracks, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.coverUrl = coverUrl;
        this.releaseDate = releaseDate;
        this.albumType = albumType;
        this.description = description;
        this.totalTracks = totalTracks;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getSlug() { return slug; }
    public String getCoverUrl() { return coverUrl; }
    public LocalDate getReleaseDate() { return releaseDate; }
    public String getAlbumType() { return albumType; }
    public String getDescription() { return description; }
    public int getTotalTracks() { return totalTracks; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
