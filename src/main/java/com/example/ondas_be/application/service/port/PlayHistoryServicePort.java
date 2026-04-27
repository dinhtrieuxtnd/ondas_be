package com.example.ondas_be.application.service.port;

import com.example.ondas_be.application.dto.common.PageResultDto;
import com.example.ondas_be.application.dto.request.RecordPlayRequest;
import com.example.ondas_be.application.dto.response.PlayHistoryResponse;

public interface PlayHistoryServicePort {

    /**
     * Records a play event for the authenticated user.
     *
     * @param email   the user's email from the security context
     * @param request play event details (songId, durationPlayedSeconds, completed, source)
     * @return the saved play history entry
     */
    PlayHistoryResponse recordPlay(String email, RecordPlayRequest request);

    /**
     * Returns the paginated play history for the authenticated user, ordered by most recent first.
     *
     * @param email the user's email from the security context
     * @param page  0-based page index
     * @param size  page size
     * @return paginated play history
     */
    PageResultDto<PlayHistoryResponse> getMyHistory(String email, int page, int size);

    /**
     * Deletes all play history entries for the authenticated user.
     *
     * @param email the user's email from the security context
     */
    void clearMyHistory(String email);

    /**
     * Deletes a specific play history entry belonging to the authenticated user.
     *
     * @param email the user's email from the security context
     * @param id    the play history entry ID
     * @throws com.example.ondas_be.application.exception.PlayHistoryNotFoundException if the entry does not exist or does not belong to the user
     */
    void deleteHistoryEntry(String email, Long id);
}
