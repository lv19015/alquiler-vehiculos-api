package org.ues.poo135.alquiler.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

/** Formato unico de todas las respuestas de error de la API. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        List<String> details
) {
    public static ApiError of(HttpStatus status, String message, String path, List<String> details) {
        return new ApiError(LocalDateTime.now(), status.value(), status.getReasonPhrase(),
                message, path, details);
    }
}
