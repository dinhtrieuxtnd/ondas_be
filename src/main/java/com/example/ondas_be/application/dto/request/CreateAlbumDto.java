package com.example.ondas_be.application.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.List;

public class CreateAlbumDto {

    @NotBlank
    private String title;

    @NotBlank
    private String slug;

    private String albumType;

    private String description;

    private LocalDate releaseDate;

    private List<AlbumTrackItemDto> tracks;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getAlbumType() { return albumType; }
    public void setAlbumType(String albumType) { this.albumType = albumType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getReleaseDate() { return releaseDate; }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }
    public List<AlbumTrackItemDto> getTracks() { return tracks; }
    public void setTracks(List<AlbumTrackItemDto> tracks) { this.tracks = tracks; }
}
