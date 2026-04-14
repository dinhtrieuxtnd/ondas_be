package com.example.ondas_be.infrastructure.persistence.model;

import com.example.ondas_be.domain.entity.Song;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
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
    private int durationSeconds;

    @Column(name = "audio_url", nullable = false)
    private String audioUrl;

    @Column(name = "audio_format", nullable = false, length = 10)
    private String audioFormat;

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
    private long playCount;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "song_genres", joinColumns = @JoinColumn(name = "song_id"))
    @Column(name = "genre_id")
    @Builder.Default
    private Set<Integer> genreIds = new HashSet<>();

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

    public Song toDomain() {
        return new Song(id, title, slug, durationSeconds, audioUrl, audioFormat, audioSizeBytes,
            coverUrl, albumId, trackNumber, releaseDate, playCount, active,
            genreIds != null ? new HashSet<>(genreIds) : new HashSet<>(),
            createdAt, updatedAt);
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
            .genreIds(song.getGenreIds() != null ? new HashSet<>(song.getGenreIds()) : new HashSet<>())
            .build();
    }
}
