package com.example.ondas_be.domain.entity;

import java.time.LocalDateTime;

// Pure domain entity — no Spring/JPA/Lombok
public class Genre {

    private final Integer id;
    private final String name;
    private final String slug;
    private final String description;
    private final String coverUrl;
    private final LocalDateTime createdAt;

    public Genre(Integer id, String name, String slug, String description, String coverUrl,
                 LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.coverUrl = coverUrl;
        this.createdAt = createdAt;
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getSlug() { return slug; }
    public String getDescription() { return description; }
    public String getCoverUrl() { return coverUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
