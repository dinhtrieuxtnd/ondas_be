package com.example.ondas_be.application.service.port;

import com.example.ondas_be.application.dto.request.CreateGenreRequest;
import com.example.ondas_be.application.dto.request.UpdateGenreRequest;
import com.example.ondas_be.application.dto.response.GenreResponse;
import com.example.ondas_be.application.dto.common.PageResultDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GenreServicePort {

    GenreResponse createGenre(CreateGenreRequest request, MultipartFile coverFile);

    GenreResponse updateGenre(Long id, UpdateGenreRequest request, MultipartFile coverFile);

    GenreResponse getGenreById(Long id);

    List<GenreResponse> getAllGenres();

    PageResultDto<GenreResponse> searchGenresByName(String query, String mode, int page, int size);

    void deleteGenre(Long id);
}
