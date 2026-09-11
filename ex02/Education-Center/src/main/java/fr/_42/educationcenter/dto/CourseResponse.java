package fr._42.educationcenter.dto;

import java.util.List;

public record CourseResponse(
        Long id,
        String name,
        String startDate,
        String endDate,
        String description,
        List<UserResponse> teachers,
        List<UserResponse> students,
        List<LessonResponse> lessons
) {
}
