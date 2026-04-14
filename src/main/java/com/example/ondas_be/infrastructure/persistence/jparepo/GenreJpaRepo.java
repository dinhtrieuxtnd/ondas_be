package com.example.ondas_be.infrastructure.persistence.jparepo;

import com.example.ondas_be.infrastructure.persistence.model.GenreModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreJpaRepo extends JpaRepository<GenreModel, Integer> {
}
