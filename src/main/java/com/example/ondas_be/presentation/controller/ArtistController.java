package com.example.ondas_be.presentation.controller;

import com.example.ondas_be.application.dto.request.CreateArtistDto;
import com.example.ondas_be.application.dto.request.UpdateArtistDto;
import com.example.ondas_be.application.dto.response.ArtistDto;
import com.example.ondas_be.application.service.port.ArtistServicePort;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/artists")
public class ArtistController {

    private final ArtistServicePort artistServicePort;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ArtistDto create(
        HttpServletRequest request,
        @RequestPart(value = "avatar", required = false) MultipartFile avatarFile
    ) throws Exception {
        Part dataPart = request.getPart("data");
        String dataJson = new String(dataPart.getInputStream().readAllBytes());
        CreateArtistDto dto = objectMapper.readValue(dataJson, CreateArtistDto.class);
        return artistServicePort.create(dto, avatarFile);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ArtistDto update(
        @PathVariable UUID id,
        HttpServletRequest request,
        @RequestPart(value = "avatar", required = false) MultipartFile avatarFile
    ) throws Exception {
        Part dataPart = request.getPart("data");
        String dataJson = new String(dataPart.getInputStream().readAllBytes());
        UpdateArtistDto dto = objectMapper.readValue(dataJson, UpdateArtistDto.class);
        return artistServicePort.update(id, dto, avatarFile);
    }

    @GetMapping("/{id}")
    public ArtistDto getById(@PathVariable UUID id) {
        return artistServicePort.getById(id);
    }

    @GetMapping
    public List<ArtistDto> getAll() {
        return artistServicePort.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        artistServicePort.delete(id);
    }
}
