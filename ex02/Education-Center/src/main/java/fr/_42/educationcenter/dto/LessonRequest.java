package fr._42.educationcenter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LessonRequest(
        @NotBlank String startTime,
        @NotBlank String finishTime,
        @NotBlank String dayOfWeek,
        @NotNull Long teacherId
) {
}
