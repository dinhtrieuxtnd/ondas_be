package com.example.ondas_be.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.UUID;

@Data
public class RecordPlayRequest {

    @NotNull(message = "Song ID is required")
    private UUID songId;

    @Pattern(
            regexp = "^(search|album|playlist|home|artist|favorites|history)$",
            message = "source must be one of: search, album, playlist, home, artist, favorites, history"
    )
    private String source;
}
