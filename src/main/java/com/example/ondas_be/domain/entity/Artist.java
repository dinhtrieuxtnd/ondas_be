package com.example.ondas_be.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

// Pure domain entity — no Spring/JPA/Lombok
public class Artist {

    private final UUID id;
    private final String name;
    private final String slug;
    private final String bio;
    private final String avatarUrl;
    private final String country;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public Artist(UUID id, String name, String slug, String bio, String avatarUrl,
                  String country, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
        this.country = country;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getSlug() { return slug; }
    public String getBio() { return bio; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getCountry() { return country; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
