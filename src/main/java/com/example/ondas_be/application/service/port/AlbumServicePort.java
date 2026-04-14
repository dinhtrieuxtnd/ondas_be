package com.example.ondas_be.application.service.port;

import com.example.ondas_be.application.dto.request.CreateAlbumDto;
import com.example.ondas_be.application.dto.request.UpdateAlbumDto;
import com.example.ondas_be.application.dto.response.AlbumDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface AlbumServicePort {

    AlbumDto create(CreateAlbumDto dto, MultipartFile coverFile);

    AlbumDto update(UUID id, UpdateAlbumDto dto, MultipartFile coverFile);

    AlbumDto getById(UUID id);

    List<AlbumDto> getAll();

    void delete(UUID id);
}
