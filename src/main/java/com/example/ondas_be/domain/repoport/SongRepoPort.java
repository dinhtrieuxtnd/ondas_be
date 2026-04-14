package com.example.ondas_be.domain.repoport;

import com.example.ondas_be.domain.entity.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SongRepoPort {

    Optional<Song> findById(UUID id);

    List<Song> findAll();

    List<Song> findAllById(List<UUID> ids);

    Song save(Song song);

    void deleteById(UUID id);

    boolean existsById(UUID id);

    void updateAlbumTrack(UUID songId, UUID albumId, Integer trackNumber);

    // New pagination + filter methods
    Page<Song> findByAlbumId(UUID albumId, Pageable pageable);

    Page<Song> findByGenreId(Integer genreId, Pageable pageable);

    Page<Song> findAllActive(Pageable pageable);

    Page<Song> findByKeyword(String keyword, Pageable pageable);

    Page<Song> findActiveByGenreId(Integer genreId, Pageable pageable);

    Page<Song> findActiveByAlbumId(UUID albumId, Pageable pageable);
}

