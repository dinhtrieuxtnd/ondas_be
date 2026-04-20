package com.example.ondas_be.infrastructure.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "song_artists")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SongArtistModel {

    @EmbeddedId
    private SongArtistId id;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String role = "main";
}
