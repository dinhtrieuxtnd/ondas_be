package com.example.ondas_be.infrastructure.persistence.model;

import com.example.ondas_be.domain.entity.Song;
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
@Table(name = "songs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SongModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, unique = true, length = 255)
    private String slug;

    @Column(name = "duration_seconds", nullable = false)
    private Integer durationSeconds;

    @Column(name = "audio_url", nullable = false)
    private String audioUrl;

    @Column(name = "audio_format", nullable = false, length = 10)
    @Builder.Default
    private String audioFormat = "mp3";

    @Column(name = "audio_size_bytes")
    private Long audioSizeBytes;

    @Column(name = "cover_url")
    private String coverUrl;

    @Column(name = "album_id")
    private UUID albumId;

    @Column(name = "track_number")
    private Integer trackNumber;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "play_count", nullable = false)
    @Builder.Default
    private Long playCount = 0L;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;

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
        if (this.playCount == null) {
            this.playCount = 0L;
        }
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Song toDomain() {
        return new Song(id, title, slug, durationSeconds, audioUrl, audioFormat, audioSizeBytes, coverUrl,
                albumId, trackNumber, releaseDate, playCount, active, createdBy, createdAt, updatedAt, null, null);
    }

    public static SongModel fromDomain(Song song) {
        return SongModel.builder()
                .id(song.getId())
                .title(song.getTitle())
                .slug(song.getSlug())
                .durationSeconds(song.getDurationSeconds())
                .audioUrl(song.getAudioUrl())
                .audioFormat(song.getAudioFormat())
                .audioSizeBytes(song.getAudioSizeBytes())
                .coverUrl(song.getCoverUrl())
                .albumId(song.getAlbumId())
                .trackNumber(song.getTrackNumber())
                .releaseDate(song.getReleaseDate())
                .playCount(song.getPlayCount())
                .active(song.isActive())
                .createdBy(song.getCreatedBy())
                .build();
    }
}
