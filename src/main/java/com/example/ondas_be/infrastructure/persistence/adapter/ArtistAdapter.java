package com.example.ondas_be.infrastructure.persistence.adapter;

import com.example.ondas_be.domain.entity.Artist;
import com.example.ondas_be.domain.repoport.ArtistRepoPort;
import com.example.ondas_be.infrastructure.persistence.jparepo.ArtistJpaRepo;
import com.example.ondas_be.infrastructure.persistence.model.ArtistModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ArtistAdapter implements ArtistRepoPort {

    private final ArtistJpaRepo artistJpaRepo;

    @Override
    public Optional<Artist> findById(UUID id) {
        return artistJpaRepo.findById(id).map(ArtistModel::toDomain);
    }

    @Override
    public List<Artist> findAll() {
        return artistJpaRepo.findAll().stream().map(ArtistModel::toDomain).toList();
    }

    @Override
    public Artist save(Artist artist) {
        ArtistModel model = ArtistModel.fromDomain(artist);
        return artistJpaRepo.save(model).toDomain();
    }

    @Override
    public void deleteById(UUID id) {
        artistJpaRepo.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return artistJpaRepo.existsById(id);
    }
}
