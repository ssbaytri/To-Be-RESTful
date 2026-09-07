package fr._42.educationcenter.controllers;

import fr._42.educationcenter.dto.CourseRequest;
import fr._42.educationcenter.dto.CourseResponse;
import fr._42.educationcenter.dto.LessonRequest;
import fr._42.educationcenter.dto.LessonResponse;
import fr._42.educationcenter.dto.UserIdRequest;
import fr._42.educationcenter.dto.UserResponse;
import fr._42.educationcenter.services.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/courses")
@Tag(name = "Courses", description = "Operations about courses")
public class CoursesController {

    private final CourseService courseService;

    public CoursesController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    @Operation(summary = "Get all courses", description = "Returns a paginated list of courses")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paginated list of courses retrieved")
    })
    public Page<CourseResponse> getCourses(Pageable pageable) {
        return courseService.getCourses(pageable);
    }

    @GetMapping("/{courseId}")
    @Operation(summary = "Get a course by id", description = "Returns a single course")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Course found"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    public CourseResponse getCourse(@PathVariable Long courseId) {
        return courseService.getCourse(courseId);
    }

    @PostMapping
    @Operation(summary = "Create a course", description = "Creates a new course and returns it")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Course created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or dates")
    })
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CourseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.createCourse(request));
    }

    @PutMapping("/{courseId}")
    @Operation(summary = "Update a course", description = "Replaces an existing course with the given data")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Course updated"),
            @ApiResponse(responseCode = "404", description = "Course not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or dates")
    })
    public CourseResponse updateCourse(@PathVariable Long courseId, @Valid @RequestBody CourseRequest request) {
        return courseService.updateCourse(courseId, request);
    }

    @DeleteMapping("/{courseId}")
    @Operation(summary = "Delete a course", description = "Removes a course by id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Course deleted"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    public ResponseEntity<Void> deleteCourse(@PathVariable Long courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{courseId}/lessons")
    @Operation(summary = "Get all lessons of a course", description = "Returns a paginated list of lessons of the given course")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paginated list of lessons retrieved"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    public Page<LessonResponse> getLessons(@PathVariable Long courseId, Pageable pageable) {
        return courseService.getLessons(courseId, pageable);
    }

    @PostMapping("/{courseId}/lessons")
    @Operation(summary = "Add a lesson to a course", description = "Creates a lesson in the given course")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Lesson created"),
            @ApiResponse(responseCode = "404", description = "Course or teacher not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or teacher does not have TEACHER role")
    })
    public ResponseEntity<LessonResponse> addLesson(@PathVariable Long courseId,
                                                    @Valid @RequestBody LessonRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(courseService.addLesson(courseId, request));
    }

    @PutMapping("/{courseId}/lessons/{lessonId}")
    @Operation(summary = "Update a lesson of a course", description = "Replaces an existing lesson of the given course")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lesson updated"),
            @ApiResponse(responseCode = "404", description = "Course or lesson not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or teacher does not have TEACHER role")
    })
    public LessonResponse updateLesson(@PathVariable Long courseId,
                                       @PathVariable Long lessonId,
                                       @Valid @RequestBody LessonRequest request) {
        return courseService.updateLesson(courseId, lessonId, request);
    }

    @DeleteMapping("/{courseId}/lessons/{lessonId}")
    @Operation(summary = "Delete a lesson of a course", description = "Removes a lesson from the given course")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Lesson deleted"),
            @ApiResponse(responseCode = "404", description = "Course or lesson not found")
    })
    public ResponseEntity<Void> deleteLesson(@PathVariable Long courseId, @PathVariable Long lessonId) {
        courseService.deleteLesson(courseId, lessonId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{courseId}/students")
    @Operation(summary = "Get all students of a course", description = "Returns a paginated list of students of the given course")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paginated list of students retrieved"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    public Page<UserResponse> getStudents(@PathVariable Long courseId, Pageable pageable) {
        return courseService.getStudents(courseId, pageable);
    }

    @PostMapping("/{courseId}/students")
    @Operation(summary = "Add a student to a course", description = "Body must contain only the user id")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Student added to the course"),
            @ApiResponse(responseCode = "404", description = "Course or user not found"),
            @ApiResponse(responseCode = "400", description = "User does not have STUDENT role")
    })
    public ResponseEntity<Void> addStudent(@PathVariable Long courseId,
                                           @Valid @RequestBody UserIdRequest request) {
        courseService.addStudent(courseId, request.id());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{courseId}/students/{studentId}")
    @Operation(summary = "Remove a student from a course", description = "Removes a student from the given course")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Student removed from the course"),
            @ApiResponse(responseCode = "404", description = "Course or user not found")
    })
    public ResponseEntity<Void> removeStudent(@PathVariable Long courseId, @PathVariable Long studentId) {
        courseService.removeStudent(courseId, studentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{courseId}/teachers")
    @Operation(summary = "Get all teachers of a course", description = "Returns a paginated list of teachers of the given course")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paginated list of teachers retrieved"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    public Page<UserResponse> getTeachers(@PathVariable Long courseId, Pageable pageable) {
        return courseService.getTeachers(courseId, pageable);
    }

    @PostMapping("/{courseId}/teachers")
    @Operation(summary = "Add a teacher to a course", description = "Body must contain only the user id")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Teacher added to the course"),
            @ApiResponse(responseCode = "404", description = "Course or user not found"),
            @ApiResponse(responseCode = "400", description = "User does not have TEACHER role")
    })
    public ResponseEntity<Void> addTeacher(@PathVariable Long courseId,
                                           @Valid @RequestBody UserIdRequest request) {
        courseService.addTeacher(courseId, request.id());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{courseId}/teachers/{teacherId}")
    @Operation(summary = "Remove a teacher from a course", description = "Removes a teacher from the given course")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Teacher removed from the course"),
            @ApiResponse(responseCode = "404", description = "Course or user not found")
    })
    public ResponseEntity<Void> removeTeacher(@PathVariable Long courseId, @PathVariable Long teacherId) {
        courseService.removeTeacher(courseId, teacherId);
        return ResponseEntity.noContent().build();
    }
}