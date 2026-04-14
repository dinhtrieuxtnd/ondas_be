package com.example.ondas_be.domain.repoport;

import com.example.ondas_be.domain.entity.Album;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AlbumRepoPort {

    Optional<Album> findById(UUID id);

    List<Album> findAll();

    Album save(Album album);

    void deleteById(UUID id);

    boolean existsById(UUID id);
}
