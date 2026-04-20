package com.example.ondas_be.infrastructure.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongArtistId implements Serializable {

    @Column(name = "song_id")
    private UUID songId;

    @Column(name = "artist_id")
    private UUID artistId;
}
