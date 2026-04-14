package com.example.ondas_be.infrastructure.persistence.jparepo;

import com.example.ondas_be.infrastructure.persistence.model.ArtistModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ArtistJpaRepo extends JpaRepository<ArtistModel, UUID> {
}
