package com.example.ondas_be.infrastructure.persistence.model;

import com.example.ondas_be.domain.entity.Artist;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "artists")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtistModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String slug;

    @Column(name = "bio")
    private String bio;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Artist toDomain() {
        return new Artist(id, name, slug, bio, avatarUrl, country, createdAt, updatedAt);
    }

    public static ArtistModel fromDomain(Artist artist) {
        return ArtistModel.builder()
            .id(artist.getId())
            .name(artist.getName())
            .slug(artist.getSlug())
            .bio(artist.getBio())
            .avatarUrl(artist.getAvatarUrl())
            .country(artist.getCountry())
            .build();
    }
}
