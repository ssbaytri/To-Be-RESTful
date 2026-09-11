package fr._42.educationcenter.dto;

import jakarta.validation.constraints.NotBlank;

public record SignUpRequest(
        @NotBlank String login,
        @NotBlank String password
) {
}