package com.example.ondas_be.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public class CreateGenreDto {

    @NotBlank
    private String name;

    @NotBlank
    private String slug;

    private String description;

    private String coverUrl;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
}
