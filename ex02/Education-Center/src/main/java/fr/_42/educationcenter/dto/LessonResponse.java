package fr._42.educationcenter.dto;

public record LessonResponse(
        Long id,
        String startTime,
        String finishTime,
        String dayOfWeek,
        UserResponse teacher
) {
}
