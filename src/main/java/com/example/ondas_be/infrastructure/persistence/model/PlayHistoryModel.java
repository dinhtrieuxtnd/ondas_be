package com.example.ondas_be.infrastructure.persistence.model;

import com.example.ondas_be.domain.entity.PlayHistory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "play_histories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayHistoryModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "song_id", nullable = false)
    private UUID songId;

    @Column(name = "played_at", nullable = false, updatable = false)
    private LocalDateTime playedAt;

    @Column(name = "duration_played_seconds")
    private Integer durationPlayedSeconds;

    @Column(name = "completed", nullable = false)
    @Builder.Default
    private Boolean completed = false;

    @Column(name = "source", length = 30)
    private String source;

    @PrePersist
    void prePersist() {
        if (this.playedAt == null) {
            this.playedAt = LocalDateTime.now();
        }
        if (this.completed == null) {
            this.completed = false;
        }
    }

    public PlayHistory toDomain() {
        return new PlayHistory(id, userId, songId, playedAt, durationPlayedSeconds, completed, source);
    }

    public static PlayHistoryModel fromDomain(PlayHistory playHistory) {
        return PlayHistoryModel.builder()
                .id(playHistory.getId())
                .userId(playHistory.getUserId())
                .songId(playHistory.getSongId())
                .playedAt(playHistory.getPlayedAt())
                .durationPlayedSeconds(playHistory.getDurationPlayedSeconds())
                .completed(playHistory.getCompleted())
                .source(playHistory.getSource())
                .build();
    }
}
