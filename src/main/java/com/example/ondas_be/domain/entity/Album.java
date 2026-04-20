package com.example.ondas_be.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class Album {

    private UUID id;
    private String title;
    private String slug;
    private String coverUrl;
    private LocalDate releaseDate;
    private String albumType;
    private String description;
    private Integer totalTracks;
    private UUID createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<UUID> artistIds;
}
