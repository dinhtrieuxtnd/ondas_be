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
@Table(name = "album_artists")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlbumArtistModel {

    @EmbeddedId
    private AlbumArtistId id;

    @Column(name = "is_primary", nullable = false)
    @Builder.Default
    private boolean primary = true;
}
