package com.example.ondas_be.domain.repoport;

import com.example.ondas_be.domain.entity.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreRepoPort {

    Optional<Genre> findById(Integer id);

    List<Genre> findAll();

    List<Genre> findAllById(List<Integer> ids);

    Genre save(Genre genre);

    void deleteById(Integer id);

    boolean existsById(Integer id);
}
