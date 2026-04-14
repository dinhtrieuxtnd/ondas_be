package com.example.ondas_be.infrastructure.persistence.model;

import com.example.ondas_be.domain.entity.Album;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "albums")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlbumModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, unique = true, length = 255)
    private String slug;

    @Column(name = "cover_url")
    private String coverUrl;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "album_type", nullable = false, length = 20)
    private String albumType;

    @Column(name = "description")
    private String description;

    @Column(name = "total_tracks", nullable = false)
    private int totalTracks;

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

    public Album toDomain() {
        return new Album(id, title, slug, coverUrl, releaseDate, albumType,
            description, totalTracks, createdAt, updatedAt);
    }

    public static AlbumModel fromDomain(Album album) {
        return AlbumModel.builder()
            .id(album.getId())
            .title(album.getTitle())
            .slug(album.getSlug())
            .coverUrl(album.getCoverUrl())
            .releaseDate(album.getReleaseDate())
            .albumType(album.getAlbumType())
            .description(album.getDescription())
            .totalTracks(album.getTotalTracks())
            .build();
    }
}
