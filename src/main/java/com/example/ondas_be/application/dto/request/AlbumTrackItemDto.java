package com.example.ondas_be.application.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class AlbumTrackItemDto {

    @NotNull
    private UUID songId;

    private Integer trackNumber;

    public UUID getSongId() { return songId; }
    public void setSongId(UUID songId) { this.songId = songId; }
    public Integer getTrackNumber() { return trackNumber; }
    public void setTrackNumber(Integer trackNumber) { this.trackNumber = trackNumber; }
}
