package com.example.ondas_be.infrastructure.persistence.jparepo;

import com.example.ondas_be.infrastructure.persistence.model.SongModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface SongJpaRepo extends JpaRepository<SongModel, UUID> {

    // Filter por album
    Page<SongModel> findByAlbumId(UUID albumId, Pageable pageable);

    // Filter by genre
    @Query("SELECT DISTINCT s FROM SongModel s JOIN s.genreIds g WHERE g = :genreId")
    Page<SongModel> findByGenreId(@Param("genreId") Integer genreId, Pageable pageable);

    // Filter by album AND active
    @Query("SELECT s FROM SongModel s WHERE s.albumId = :albumId AND s.active = true")
    Page<SongModel> findActiveByAlbumId(@Param("albumId") UUID albumId, Pageable pageable);

    // Filter by genre AND active
    @Query("SELECT DISTINCT s FROM SongModel s WHERE :genreId MEMBER OF s.genreIds AND s.active = true")
    Page<SongModel> findActiveByGenreId(@Param("genreId") Integer genreId, Pageable pageable);

    // Search by title + active
    @Query("SELECT s FROM SongModel s WHERE (LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(s.slug) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND s.active = true")
    Page<SongModel> findActiveByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // Get all active songs
    @Query("SELECT s FROM SongModel s WHERE s.active = true")
    Page<SongModel> findAllActive(Pageable pageable);
}


