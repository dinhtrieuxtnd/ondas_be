package com.example.ondas_be.application.dto.response;

import java.io.InputStream;

/**
 * Holds all metadata required to build an HTTP streaming response for a song.
 *
 * @param audioStream  the byte stream of the audio content
 * @param totalSize    total byte size of the audio file (may be -1 if unknown)
 * @param rangeStart   first byte position that will be served
 * @param rangeEnd     last byte position that will be served (inclusive)
 * @param contentType  MIME type of the audio (e.g. "audio/mpeg")
 * @param isPartial    true when this is a range response (HTTP 206), false for full content (HTTP 200)
 */
public record SongStreamResponse(
        InputStream audioStream,
        long totalSize,
        long rangeStart,
        long rangeEnd,
        String contentType,
        boolean isPartial
) {}
