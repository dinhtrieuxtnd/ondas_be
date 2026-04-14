package com.example.ondas_be.infrastructure.persistence.adapter;

import com.example.ondas_be.domain.entity.Album;
import com.example.ondas_be.domain.repoport.AlbumRepoPort;
import com.example.ondas_be.infrastructure.persistence.jparepo.AlbumJpaRepo;
import com.example.ondas_be.infrastructure.persistence.model.AlbumModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AlbumAdapter implements AlbumRepoPort {

    private final AlbumJpaRepo albumJpaRepo;

    @Override
    public Optional<Album> findById(UUID id) {
        return albumJpaRepo.findById(id).map(AlbumModel::toDomain);
    }

    @Override
    public List<Album> findAll() {
        return albumJpaRepo.findAll().stream().map(AlbumModel::toDomain).toList();
    }

    @Override
    public Album save(Album album) {
        AlbumModel model = AlbumModel.fromDomain(album);
        return albumJpaRepo.save(model).toDomain();
    }

    @Override
    public void deleteById(UUID id) {
        albumJpaRepo.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return albumJpaRepo.existsById(id);
    }
}
