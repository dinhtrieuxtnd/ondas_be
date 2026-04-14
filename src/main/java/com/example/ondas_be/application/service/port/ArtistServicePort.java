package com.example.ondas_be.application.service.port;

import com.example.ondas_be.application.dto.request.CreateArtistDto;
import com.example.ondas_be.application.dto.request.UpdateArtistDto;
import com.example.ondas_be.application.dto.response.ArtistDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ArtistServicePort {

    ArtistDto create(CreateArtistDto dto, MultipartFile avatarFile);

    ArtistDto update(UUID id, UpdateArtistDto dto, MultipartFile avatarFile);

    ArtistDto getById(UUID id);

    List<ArtistDto> getAll();

    void delete(UUID id);
}
