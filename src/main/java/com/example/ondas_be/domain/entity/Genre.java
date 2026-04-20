package com.example.ondas_be.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class Genre {

    private Long id;
    private String name;
    private String slug;
    private String description;
    private String coverUrl;
    private LocalDateTime createdAt;
}
