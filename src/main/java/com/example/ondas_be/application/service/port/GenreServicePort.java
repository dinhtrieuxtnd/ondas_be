package com.example.ondas_be.application.service.port;

import com.example.ondas_be.application.dto.request.CreateGenreDto;
import com.example.ondas_be.application.dto.request.UpdateGenreDto;
import com.example.ondas_be.application.dto.response.GenreDto;

import java.util.List;

public interface GenreServicePort {

    GenreDto create(CreateGenreDto dto);

    GenreDto update(Integer id, UpdateGenreDto dto);

    GenreDto getById(Integer id);

    List<GenreDto> getAll();

    void delete(Integer id);
}
