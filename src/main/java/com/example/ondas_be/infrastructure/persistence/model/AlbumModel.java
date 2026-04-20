package com.example.ondas_be.infrastructure.persistence.model;

import com.example.ondas_be.domain.entity.Album;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @Builder.Default
    private String albumType = "album";

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "total_tracks", nullable = false)
    @Builder.Default
    private Integer totalTracks = 0;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.totalTracks == null) {
            this.totalTracks = 0;
        }
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Album toDomain() {
        return new Album(id, title, slug, coverUrl, releaseDate, albumType, description, totalTracks,
                createdBy, createdAt, updatedAt, null);
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
                .createdBy(album.getCreatedBy())
                .build();
    }
}
