package com.example.ondas_be.presentation.controller;

import com.example.ondas_be.application.dto.request.CreateGenreDto;
import com.example.ondas_be.application.dto.request.UpdateGenreDto;
import com.example.ondas_be.application.dto.response.GenreDto;
import com.example.ondas_be.application.service.port.GenreServicePort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/genres")
public class GenreController {

    private final GenreServicePort genreServicePort;

    @PostMapping
    public GenreDto create(@Valid @RequestBody CreateGenreDto dto) {
        return genreServicePort.create(dto);
    }

    @PutMapping("/{id}")
    public GenreDto update(@PathVariable Integer id, @Valid @RequestBody UpdateGenreDto dto) {
        return genreServicePort.update(id, dto);
    }

    @GetMapping("/{id}")
    public GenreDto getById(@PathVariable Integer id) {
        return genreServicePort.getById(id);
    }

    @GetMapping
    public List<GenreDto> getAll() {
        return genreServicePort.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        genreServicePort.delete(id);
    }
}
