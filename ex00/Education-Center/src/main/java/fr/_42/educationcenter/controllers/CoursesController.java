package fr._42.educationcenter.controllers;

import fr._42.educationcenter.dto.CourseRequest;
import fr._42.educationcenter.dto.CourseResponse;
import fr._42.educationcenter.dto.LessonRequest;
import fr._42.educationcenter.dto.LessonResponse;
import fr._42.educationcenter.dto.UserIdRequest;
import fr._42.educationcenter.dto.UserResponse;
import fr._42.educationcenter.services.CourseService;
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
public class CoursesController {

    private final CourseService courseService;

    public CoursesController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public Page<CourseResponse> getCourses(Pageable pageable) {
        return courseService.getCourses(pageable);
    }

    @GetMapping("/{courseId}")
    public CourseResponse getCourse(@PathVariable Long courseId) {
        return courseService.getCourse(courseId);
    }

    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CourseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.createCourse(request));
    }

    @PutMapping("/{courseId}")
    public CourseResponse updateCourse(@PathVariable Long courseId, @Valid @RequestBody CourseRequest request) {
        return courseService.updateCourse(courseId, request);
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{courseId}/lessons")
    public Page<LessonResponse> getLessons(@PathVariable Long courseId, Pageable pageable) {
        return courseService.getLessons(courseId, pageable);
    }

    @PostMapping("/{courseId}/lessons")
    public ResponseEntity<LessonResponse> addLesson(@PathVariable Long courseId,
                                                    @Valid @RequestBody LessonRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(courseService.addLesson(courseId, request));
    }

    @PutMapping("/{courseId}/lessons/{lessonId}")
    public LessonResponse updateLesson(@PathVariable Long courseId,
                                       @PathVariable Long lessonId,
                                       @Valid @RequestBody LessonRequest request) {
        return courseService.updateLesson(courseId, lessonId, request);
    }

    @DeleteMapping("/{courseId}/lessons/{lessonId}")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long courseId, @PathVariable Long lessonId) {
        courseService.deleteLesson(courseId, lessonId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{courseId}/students")
    public Page<UserResponse> getStudents(@PathVariable Long courseId, Pageable pageable) {
        return courseService.getStudents(courseId, pageable);
    }

    @PostMapping("/{courseId}/students")
    public ResponseEntity<Void> addStudent(@PathVariable Long courseId,
                                           @Valid @RequestBody UserIdRequest request) {
        courseService.addStudent(courseId, request.id());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{courseId}/students/{studentId}")
    public ResponseEntity<Void> removeStudent(@PathVariable Long courseId, @PathVariable Long studentId) {
        courseService.removeStudent(courseId, studentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{courseId}/teachers")
    public Page<UserResponse> getTeachers(@PathVariable Long courseId, Pageable pageable) {
        return courseService.getTeachers(courseId, pageable);
    }

    @PostMapping("/{courseId}/teachers")
    public ResponseEntity<Void> addTeacher(@PathVariable Long courseId,
                                           @Valid @RequestBody UserIdRequest request) {
        courseService.addTeacher(courseId, request.id());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{courseId}/teachers/{teacherId}")
    public ResponseEntity<Void> removeTeacher(@PathVariable Long courseId, @PathVariable Long teacherId) {
        courseService.removeTeacher(courseId, teacherId);
        return ResponseEntity.noContent().build();
    }
}