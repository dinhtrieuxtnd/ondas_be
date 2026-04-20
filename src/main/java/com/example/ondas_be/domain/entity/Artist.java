package com.example.ondas_be.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class Artist {

    private UUID id;
    private String name;
    private String slug;
    private String bio;
    private String avatarUrl;
    private String country;
    private UUID createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
