package com.example.ondas_be.application.service.port;

import com.example.ondas_be.application.dto.request.CreateSongRequest;
import com.example.ondas_be.application.dto.request.SongFilterRequest;
import com.example.ondas_be.application.dto.request.UpdateSongRequest;
import com.example.ondas_be.application.dto.response.SongResponse;
import com.example.ondas_be.application.dto.response.SongStreamResponse;
import com.example.ondas_be.application.dto.common.PageResultDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface SongServicePort {

    SongResponse createSong(CreateSongRequest request, MultipartFile audioFile, MultipartFile coverFile);

    SongResponse updateSong(UUID id, UpdateSongRequest request, MultipartFile audioFile, MultipartFile coverFile);

    SongResponse getSongById(UUID id);

    PageResultDto<SongResponse> getSongs(SongFilterRequest filter);

    void deleteSong(UUID id);

    /**
     * Streams audio bytes for a song, honouring the HTTP Range header.
     * Records play history and increments the play count when the range starts at byte 0.
     *
     * @param id          song identifier
     * @param rangeHeader value of the {@code Range} HTTP header (may be null)
     * @param email       authenticated user's email
     * @param source      play source (e.g. "playlist", "home") — may be null
     * @return streaming metadata and the open InputStream
     */
    SongStreamResponse streamSong(UUID id, String rangeHeader, String email, String source);
}
