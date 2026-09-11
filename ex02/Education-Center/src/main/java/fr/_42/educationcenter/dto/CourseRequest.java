package fr._42.educationcenter.dto;

import jakarta.validation.constraints.NotBlank;

public record CourseRequest(
        @NotBlank String name,
        @NotBlank String startDate,
        @NotBlank String endDate,
        String description
) {
}
