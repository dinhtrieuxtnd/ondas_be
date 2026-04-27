package com.example.ondas_be.domain.repoport;

import com.example.ondas_be.domain.entity.PlayHistory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlayHistoryRepoPort {

    PlayHistory save(PlayHistory playHistory);

    List<PlayHistory> findByUserId(UUID userId, int page, int size);

    long countByUserId(UUID userId);

    Optional<PlayHistory> findByIdAndUserId(Long id, UUID userId);

    void deleteAllByUserId(UUID userId);

    void deleteByIdAndUserId(Long id, UUID userId);
}
