package com.example.ondas_be.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayHistoryResponse {

    private Long id;
    private PlayHistorySongInfo song;
    private LocalDateTime playedAt;
    private Integer durationPlayedSeconds;
    private Boolean completed;
    private String source;
}
