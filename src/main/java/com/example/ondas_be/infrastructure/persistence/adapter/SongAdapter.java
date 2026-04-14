package com.example.ondas_be.infrastructure.persistence.adapter;

import com.example.ondas_be.domain.entity.Song;
import com.example.ondas_be.domain.repoport.SongRepoPort;
import com.example.ondas_be.infrastructure.persistence.jparepo.SongJpaRepo;
import com.example.ondas_be.infrastructure.persistence.model.SongModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SongAdapter implements SongRepoPort {

    private final SongJpaRepo songJpaRepo;

    @Override
    public Optional<Song> findById(UUID id) {
        return songJpaRepo.findById(id).map(SongModel::toDomain);
    }

    @Override
    public List<Song> findAll() {
        return songJpaRepo.findAll().stream().map(SongModel::toDomain).toList();
    }

    @Override
    public List<Song> findAllById(List<UUID> ids) {
        return songJpaRepo.findAllById(ids).stream().map(SongModel::toDomain).toList();
    }

    @Override
    public Song save(Song song) {
        SongModel model = SongModel.fromDomain(song);
        return songJpaRepo.save(model).toDomain();
    }

    @Override
    public void deleteById(UUID id) {
        songJpaRepo.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return songJpaRepo.existsById(id);
    }

    @Override
    public void updateAlbumTrack(UUID songId, UUID albumId, Integer trackNumber) {
        SongModel model = songJpaRepo.findById(songId)
            .orElseThrow(() -> new IllegalArgumentException("song not found"));
        model.setAlbumId(albumId);
        model.setTrackNumber(trackNumber);
        songJpaRepo.save(model);
    }

    @Override
    public Page<Song> findByAlbumId(UUID albumId, Pageable pageable) {
        return songJpaRepo.findByAlbumId(albumId, pageable).map(SongModel::toDomain);
    }

    @Override
    public Page<Song> findByGenreId(Integer genreId, Pageable pageable) {
        return songJpaRepo.findByGenreId(genreId, pageable).map(SongModel::toDomain);
    }

    @Override
    public Page<Song> findAllActive(Pageable pageable) {
        return songJpaRepo.findAllActive(pageable).map(SongModel::toDomain);
    }

    @Override
    public Page<Song> findByKeyword(String keyword, Pageable pageable) {
        return songJpaRepo.findActiveByKeyword(keyword, pageable).map(SongModel::toDomain);
    }

    @Override
    public Page<Song> findActiveByGenreId(Integer genreId, Pageable pageable) {
        return songJpaRepo.findActiveByGenreId(genreId, pageable).map(SongModel::toDomain);
    }

    @Override
    public Page<Song> findActiveByAlbumId(UUID albumId, Pageable pageable) {
        return songJpaRepo.findActiveByAlbumId(albumId, pageable).map(SongModel::toDomain);
    }
}

