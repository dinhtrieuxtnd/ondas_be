package com.example.ondas_be.infrastructure.persistence.adapter;

import com.example.ondas_be.domain.repoport.SongArtistRepoPort;
import com.example.ondas_be.infrastructure.persistence.jparepo.SongArtistJpaRepo;
import com.example.ondas_be.infrastructure.persistence.model.SongArtistId;
import com.example.ondas_be.infrastructure.persistence.model.SongArtistModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SongArtistAdapter implements SongArtistRepoPort {

    private final SongArtistJpaRepo songArtistJpaRepo;

    @Override
    public void replaceSongArtists(UUID songId, List<UUID> artistIds) {
        songArtistJpaRepo.deleteByIdSongId(songId);
        if (artistIds == null || artistIds.isEmpty()) {
            return;
        }
        List<SongArtistModel> models = artistIds.stream()
                .map(artistId -> SongArtistModel.builder()
                        .id(new SongArtistId(songId, artistId))
                        .role("main")
                        .build())
                .toList();
        songArtistJpaRepo.saveAll(models);
    }

    @Override
    public List<UUID> findArtistIdsBySongId(UUID songId) {
        List<SongArtistModel> models = songArtistJpaRepo.findByIdSongId(songId);
        if (models == null || models.isEmpty()) {
            return Collections.emptyList();
        }
        return models.stream().map(model -> model.getId().getArtistId()).toList();
    }
}
