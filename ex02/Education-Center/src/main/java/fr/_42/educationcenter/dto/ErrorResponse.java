package fr._42.educationcenter.dto;

public record ErrorResponse(
        int status,
        String message
) {
}
