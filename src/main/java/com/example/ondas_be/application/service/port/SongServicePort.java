package com.example.ondas_be.application.service.port;

import com.example.ondas_be.application.dto.request.CreateSongRequest;
import com.example.ondas_be.application.dto.request.UpdateSongRequest;
import com.example.ondas_be.application.dto.response.SongResponse;
import com.example.ondas_be.application.dto.common.PageResultDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface SongServicePort {

    SongResponse createSong(CreateSongRequest request, MultipartFile audioFile, MultipartFile coverFile);

    SongResponse updateSong(UUID id, UpdateSongRequest request, MultipartFile audioFile, MultipartFile coverFile);

    SongResponse getSongById(UUID id);

    List<SongResponse> getAllSongs();

    PageResultDto<SongResponse> getSongsByArtist(UUID artistId, int page, int size);

    PageResultDto<SongResponse> getSongsByAlbum(UUID albumId, int page, int size);

    PageResultDto<SongResponse> getSongsByGenre(Long genreId, int page, int size);

    PageResultDto<SongResponse> searchSongsByTitle(String query, String mode, int page, int size);

    void deleteSong(UUID id);
}
