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
public class SongGenreId implements Serializable {

    @Column(name = "song_id")
    private UUID songId;

    @Column(name = "genre_id")
    private Long genreId;
}
