package com.example.ondas_be.infrastructure.persistence.jparepo;

import com.example.ondas_be.infrastructure.persistence.model.GenreModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GenreJpaRepo extends JpaRepository<GenreModel, Long> {

    boolean existsBySlug(String slug);

    @Query(value = "select * from genres g where g.name ilike concat('%', :query, '%')",
        countQuery = "select count(*) from genres g where g.name ilike concat('%', :query, '%')",
        nativeQuery = true)
    Page<GenreModel> findByNameContains(@Param("query") String query, Pageable pageable);

    @Query(value = "select count(*) from genres g where g.name ilike concat('%', :query, '%')", nativeQuery = true)
    long countByNameContains(@Param("query") String query);

    @Query(value = "select * from genres g where to_tsvector('simple', g.name) @@ plainto_tsquery('simple', :query)",
        countQuery = "select count(*) from genres g where to_tsvector('simple', g.name) @@ plainto_tsquery('simple', :query)",
        nativeQuery = true)
    Page<GenreModel> findByNameFullText(@Param("query") String query, Pageable pageable);

    @Query(value = "select count(*) from genres g where to_tsvector('simple', g.name) @@ plainto_tsquery('simple', :query)",
        nativeQuery = true)
    long countByNameFullText(@Param("query") String query);
}
