package com.example.ondas_be.application.mapper;

import com.example.ondas_be.application.dto.response.SongResponse;
import com.example.ondas_be.application.dto.response.SongSummaryResponse;
import com.example.ondas_be.domain.entity.Song;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SongMapper {

    SongResponse toResponse(Song song);

    SongSummaryResponse toSummaryResponse(Song song);

    List<SongSummaryResponse> toSummaryResponseList(List<Song> songs);
}
