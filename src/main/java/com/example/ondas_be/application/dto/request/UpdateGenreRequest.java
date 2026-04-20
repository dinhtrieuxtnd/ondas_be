package com.example.ondas_be.application.dto.request;

import lombok.Data;

@Data
public class UpdateGenreRequest {

    private String name;
    private String slug;
    private String description;
    private String coverUrl;
}
