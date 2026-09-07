package fr._42.educationcenter.dto;

import jakarta.validation.constraints.NotBlank;

public record UserRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String role,
        @NotBlank String login,
        @NotBlank String password
) {
}
