package com.example.ondas_be.infrastructure.persistence.jparepo;

import com.example.ondas_be.infrastructure.persistence.model.PlayHistoryModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PlayHistoryJpaRepo extends JpaRepository<PlayHistoryModel, Long> {

    Page<PlayHistoryModel> findByUserIdOrderByPlayedAtDesc(UUID userId, Pageable pageable);

    long countByUserId(UUID userId);

    Optional<PlayHistoryModel> findByIdAndUserId(Long id, UUID userId);

    @Modifying
    @Query("delete from PlayHistoryModel p where p.userId = :userId")
    void deleteAllByUserId(@Param("userId") UUID userId);

    @Modifying
    @Query("delete from PlayHistoryModel p where p.id = :id and p.userId = :userId")
    void deleteByIdAndUserId(@Param("id") Long id, @Param("userId") UUID userId);
}
