package com.example.ondas_be.domain.repoport;

import java.util.List;
import java.util.UUID;

public interface SongGenreRepoPort {

    void replaceSongGenres(UUID songId, List<Long> genreIds);

    List<Long> findGenreIdsBySongId(UUID songId);
}
