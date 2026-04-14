package com.example.ondas_be.infrastructure.persistence.jparepo;

import com.example.ondas_be.infrastructure.persistence.model.AlbumModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AlbumJpaRepo extends JpaRepository<AlbumModel, UUID> {
}
