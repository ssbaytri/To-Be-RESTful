package fr._42.educationcenter.dto;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String role,
        String login
) {
}
