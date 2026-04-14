package com.example.ondas_be.presentation.controller;

import com.example.ondas_be.application.dto.request.CreateAlbumDto;
import com.example.ondas_be.application.dto.request.UpdateAlbumDto;
import com.example.ondas_be.application.dto.response.AlbumDto;
import com.example.ondas_be.application.service.port.AlbumServicePort;
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
@RequestMapping("/api/admin/albums")
public class AlbumController {

    private final AlbumServicePort albumServicePort;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AlbumDto create(
        HttpServletRequest request,
        @RequestPart(value = "cover", required = false) MultipartFile coverFile
    ) throws Exception {
        Part dataPart = request.getPart("data");
        String dataJson = new String(dataPart.getInputStream().readAllBytes());
        CreateAlbumDto dto = objectMapper.readValue(dataJson, CreateAlbumDto.class);
        return albumServicePort.create(dto, coverFile);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AlbumDto update(
        @PathVariable UUID id,
        HttpServletRequest request,
        @RequestPart(value = "cover", required = false) MultipartFile coverFile
    ) throws Exception {
        Part dataPart = request.getPart("data");
        String dataJson = new String(dataPart.getInputStream().readAllBytes());
        UpdateAlbumDto dto = objectMapper.readValue(dataJson, UpdateAlbumDto.class);
        return albumServicePort.update(id, dto, coverFile);
    }

    @GetMapping("/{id}")
    public AlbumDto getById(@PathVariable UUID id) {
        return albumServicePort.getById(id);
    }

    @GetMapping
    public List<AlbumDto> getAll() {
        return albumServicePort.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        albumServicePort.delete(id);
    }
}
