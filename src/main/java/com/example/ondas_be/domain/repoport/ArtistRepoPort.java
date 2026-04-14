package com.example.ondas_be.domain.repoport;

import com.example.ondas_be.domain.entity.Artist;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArtistRepoPort {

    Optional<Artist> findById(UUID id);

    List<Artist> findAll();

    Artist save(Artist artist);

    void deleteById(UUID id);

    boolean existsById(UUID id);
}
