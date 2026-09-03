package fr._42.educationcenter.dto;

import jakarta.validation.constraints.NotNull;

public record UserIdRequest(@NotNull Long id) {
}
