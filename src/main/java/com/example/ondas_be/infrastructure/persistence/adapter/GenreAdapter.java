package com.example.ondas_be.infrastructure.persistence.adapter;

import com.example.ondas_be.domain.entity.Genre;
import com.example.ondas_be.domain.repoport.GenreRepoPort;
import com.example.ondas_be.infrastructure.persistence.jparepo.GenreJpaRepo;
import com.example.ondas_be.infrastructure.persistence.model.GenreModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GenreAdapter implements GenreRepoPort {

    private final GenreJpaRepo genreJpaRepo;

    @Override
    public Optional<Genre> findById(Integer id) {
        return genreJpaRepo.findById(id).map(GenreModel::toDomain);
    }

    @Override
    public List<Genre> findAll() {
        return genreJpaRepo.findAll().stream().map(GenreModel::toDomain).toList();
    }

    @Override
    public List<Genre> findAllById(List<Integer> ids) {
        return genreJpaRepo.findAllById(ids).stream().map(GenreModel::toDomain).toList();
    }

    @Override
    public Genre save(Genre genre) {
        GenreModel model = GenreModel.fromDomain(genre);
        return genreJpaRepo.save(model).toDomain();
    }

    @Override
    public void deleteById(Integer id) {
        genreJpaRepo.deleteById(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return genreJpaRepo.existsById(id);
    }
}
