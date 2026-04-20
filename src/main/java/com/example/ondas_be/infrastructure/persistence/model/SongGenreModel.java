package com.example.ondas_be.infrastructure.persistence.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "song_genres")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SongGenreModel {

    @EmbeddedId
    private SongGenreId id;
}
