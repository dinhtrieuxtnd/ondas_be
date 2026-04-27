package com.example.ondas_be.unit.service;

import com.example.ondas_be.application.dto.request.CreateAlbumRequest;
import com.example.ondas_be.application.dto.response.AlbumResponse;
import com.example.ondas_be.application.dto.response.SongSummaryResponse;
import com.example.ondas_be.application.mapper.AlbumMapper;
import com.example.ondas_be.application.mapper.ArtistMapper;
import com.example.ondas_be.application.mapper.SongMapper;
import com.example.ondas_be.application.service.impl.AlbumService;
import com.example.ondas_be.application.service.port.StoragePort;
import com.example.ondas_be.domain.entity.Album;
import com.example.ondas_be.domain.entity.Song;
import com.example.ondas_be.domain.repoport.AlbumArtistRepoPort;
import com.example.ondas_be.domain.repoport.AlbumRepoPort;
import com.example.ondas_be.domain.repoport.ArtistRepoPort;
import com.example.ondas_be.domain.repoport.SongRepoPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

    @Mock
    private AlbumRepoPort albumRepoPort;

    @Mock
    private ArtistRepoPort artistRepoPort;

    @Mock
    private AlbumArtistRepoPort albumArtistRepoPort;

    @Mock
    private SongRepoPort songRepoPort;

    @Mock
    private StoragePort storagePort;

    @Mock
    private AlbumMapper albumMapper;

    @Mock
    private ArtistMapper artistMapper;

    @Mock
    private SongMapper songMapper;

    @InjectMocks
    private AlbumService albumService;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(albumService, "imageBucket", "ondas-images");
    }

    @Test
    void createAlbum_WhenValid_ShouldUploadCoverAndSave() {
        UUID artistId = UUID.randomUUID();

        CreateAlbumRequest request = new CreateAlbumRequest();
        request.setTitle("Album A");
        request.setSlug("album-a");
        request.setArtistIds(List.of(artistId));

        MockMultipartFile cover = new MockMultipartFile(
                "cover",
                "cover.jpg",
                "image/jpeg",
                "cover".getBytes());

        when(artistRepoPort.existsById(artistId)).thenReturn(true);
        when(albumRepoPort.existsBySlug(any())).thenReturn(false);
        when(storagePort.upload(eq("ondas-images"), any(), any(), anyLong(), any()))
                .thenReturn("cover-url");
        when(albumRepoPort.save(any(Album.class))).thenAnswer(invocation -> {
            Album input = invocation.getArgument(0);
            return new Album(UUID.randomUUID(), input.getTitle(), input.getSlug(), input.getCoverUrl(),
                    input.getReleaseDate(), input.getAlbumType(), input.getDescription(), 0,
                    input.getCreatedBy(), LocalDateTime.now(), LocalDateTime.now(), input.getArtistIds());
        });
        when(albumMapper.toResponse(any(Album.class))).thenReturn(new AlbumResponse());

        albumService.createAlbum(request, cover);

        verify(storagePort).upload(eq("ondas-images"), any(), any(), anyLong(), any());
        verify(albumArtistRepoPort).replaceAlbumArtists(any(), eq(List.of(artistId)));
    }

    @Test
    void getAlbumById_ShouldReturnTracklist() {
        UUID albumId = UUID.randomUUID();
        UUID artistId = UUID.randomUUID();
        Album album = new Album(albumId, "Album", "album", null, null, "album", null, 0,
                null, LocalDateTime.now(), LocalDateTime.now(), List.of(artistId));

        when(albumRepoPort.findById(albumId)).thenReturn(java.util.Optional.of(album));
        when(albumArtistRepoPort.findArtistIdsByAlbumId(albumId)).thenReturn(List.of(artistId));
        when(songRepoPort.findByAlbumIdOrderByTrackNumber(albumId)).thenReturn(List.of(
                new Song(UUID.randomUUID(), "Song", "song", 200, "url", "mp3", 100L, null,
                        albumId, 1, null, 0L, true, null, LocalDateTime.now(), LocalDateTime.now(),
                        List.of(artistId), List.of(1L))
        ));
        when(songMapper.toSummaryResponseList(any())).thenReturn(List.of(
                new SongSummaryResponse(UUID.randomUUID(), "Song", 1, 200, "url")
        ));
        when(albumMapper.toResponse(any(Album.class))).thenReturn(new AlbumResponse());

        AlbumResponse response = albumService.getAlbumById(albumId);

        assertEquals(1, response.getTracklist().size());
        assertEquals(1, response.getArtistIds().size());
    }
}
