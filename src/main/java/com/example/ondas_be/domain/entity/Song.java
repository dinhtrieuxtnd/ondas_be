package com.example.ondas_be.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class Song {

    private UUID id;
    private String title;
    private String slug;
    private Integer durationSeconds;
    private String audioUrl;
    private String audioFormat;
    private Long audioSizeBytes;
    private String coverUrl;
    private UUID albumId;
    private Integer trackNumber;
    private LocalDate releaseDate;
    private Long playCount;
    private boolean active;
    private UUID createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<UUID> artistIds;
    private List<Long> genreIds;
}
