package com.example.ondas_be.unit.service;

import com.example.ondas_be.application.dto.request.CreateArtistRequest;
import com.example.ondas_be.application.dto.request.UpdateArtistRequest;
import com.example.ondas_be.application.dto.response.ArtistResponse;
import com.example.ondas_be.application.mapper.ArtistMapper;
import com.example.ondas_be.application.service.impl.ArtistService;
import com.example.ondas_be.application.service.port.StoragePort;
import com.example.ondas_be.domain.entity.Artist;
import com.example.ondas_be.domain.repoport.ArtistRepoPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArtistServiceTest {

    @Mock
    private ArtistRepoPort artistRepoPort;

    @Mock
    private StoragePort storagePort;

    @Mock
    private ArtistMapper artistMapper;

    @InjectMocks
    private ArtistService artistService;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(artistService, "imageBucket", "ondas-images");
    }

    @Test
    void createArtist_WhenAvatarProvided_ShouldUploadAndSave() {
        CreateArtistRequest request = new CreateArtistRequest();
        request.setName("Artist Name");
        request.setSlug("artist-name");

        MockMultipartFile avatar = new MockMultipartFile(
                "avatar",
                "avatar.jpg",
                "image/jpeg",
                "avatar".getBytes());

        when(artistRepoPort.existsBySlug(any())).thenReturn(false);
        when(storagePort.upload(eq("ondas-images"), any(), any(), anyLong(), any()))
                .thenReturn("avatar-url");
        when(artistRepoPort.save(any(Artist.class))).thenAnswer(invocation -> {
            Artist input = invocation.getArgument(0);
            return new Artist(UUID.randomUUID(), input.getName(), input.getSlug(), input.getBio(),
                    input.getAvatarUrl(), input.getCountry(), input.getCreatedBy(),
                    LocalDateTime.now(), LocalDateTime.now());
        });
        when(artistMapper.toResponse(any(Artist.class))).thenReturn(new ArtistResponse());

        artistService.createArtist(request, avatar);

        verify(storagePort).upload(eq("ondas-images"), any(), any(), anyLong(), any());
        verify(artistRepoPort).save(any(Artist.class));
    }

    @Test
    void updateArtist_WhenAvatarProvided_ShouldDeleteOldAvatar() {
        UUID artistId = UUID.randomUUID();
        Artist existing = new Artist(
                artistId,
                "Old Artist",
                "old-artist",
                null,
                "old-avatar-url",
                null,
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        UpdateArtistRequest request = new UpdateArtistRequest();
        request.setName("New Artist");

        MockMultipartFile avatar = new MockMultipartFile(
                "avatar",
                "new.jpg",
                "image/jpeg",
                "new".getBytes());

        when(artistRepoPort.findById(artistId)).thenReturn(java.util.Optional.of(existing));
        when(artistRepoPort.existsBySlug(any())).thenReturn(false);
        when(storagePort.upload(eq("ondas-images"), any(), any(), anyLong(), any()))
                .thenReturn("new-avatar-url");
        when(storagePort.extractObjectName(eq("ondas-images"), eq("old-avatar-url"))).thenReturn("old.jpg");
        when(artistRepoPort.save(any(Artist.class))).thenReturn(existing);
        when(artistMapper.toResponse(any(Artist.class))).thenReturn(new ArtistResponse());

        ArtistResponse response = artistService.updateArtist(artistId, request, avatar);

        assertEquals(response.getClass(), ArtistResponse.class);
        verify(storagePort).delete("ondas-images", "old.jpg");
    }
}
