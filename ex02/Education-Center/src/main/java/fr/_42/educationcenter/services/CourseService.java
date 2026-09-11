package fr._42.educationcenter.services;

import fr._42.educationcenter.dto.CourseRequest;
import fr._42.educationcenter.dto.CourseResponse;
import fr._42.educationcenter.dto.LessonRequest;
import fr._42.educationcenter.dto.LessonResponse;
import fr._42.educationcenter.dto.UserResponse;
import fr._42.educationcenter.exceptions.BadRequestException;
import fr._42.educationcenter.exceptions.NotFoundException;
import fr._42.educationcenter.models.Course;
import fr._42.educationcenter.models.Lesson;
import fr._42.educationcenter.models.User;
import fr._42.educationcenter.models.UserRole;
import fr._42.educationcenter.repositories.CourseRepository;
import fr._42.educationcenter.repositories.LessonRepository;
import fr._42.educationcenter.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class CourseService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;

    public CourseService(CourseRepository courseRepository,
                         LessonRepository lessonRepository,
                         UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.userRepository = userRepository;
    }

    public Page<CourseResponse> getCourses(Pageable pageable) {
        return courseRepository.findAll(pageable).map(CourseService::toResponse);
    }

    public CourseResponse getCourse(Long id) {
        return toResponse(findCourse(id));
    }

    @Transactional
    public CourseResponse createCourse(CourseRequest request) {
        Course course = new Course();
        course.setName(request.name());
        course.setStartDate(parseDate(request.startDate()));
        course.setEndDate(parseDate(request.endDate()));
        course.setDescription(request.description());
        return toResponse(courseRepository.save(course));
    }

    @Transactional
    public CourseResponse updateCourse(Long id, CourseRequest request) {
        Course course = findCourse(id);
        course.setName(request.name());
        course.setStartDate(parseDate(request.startDate()));
        course.setEndDate(parseDate(request.endDate()));
        course.setDescription(request.description());
        return toResponse(course);
    }

    @Transactional
    public void deleteCourse(Long id) {
        Course course = findCourse(id);
        courseRepository.delete(course);
    }

    public Page<LessonResponse> getLessons(Long courseId, Pageable pageable) {
        findCourse(courseId);
        return lessonRepository.findByCourseId(courseId, pageable)
                .map(CourseService::toLessonResponse);
    }

    @Transactional
    public LessonResponse addLesson(Long courseId, LessonRequest request) {
        Course course = findCourse(courseId);
        User teacher = findUser(request.teacherId());
        if (teacher.getRole() != UserRole.TEACHER) {
            throw new BadRequestException("Teacher must have TEACHER role");
        }
        Lesson lesson = new Lesson();
        lesson.setStartTime(parseTime(request.startTime()));
        lesson.setFinishTime(parseTime(request.finishTime()));
        lesson.setDayOfWeek(parseDayOfWeek(request.dayOfWeek()));
        lesson.setTeacher(teacher);
        lesson.setCourse(course);
        return toLessonResponse(lessonRepository.save(lesson));
    }

    @Transactional
    public LessonResponse updateLesson(Long courseId, Long lessonId, LessonRequest request) {
        findCourse(courseId);
        Lesson lesson = findLesson(lessonId);
        if (!lesson.getCourse().getId().equals(courseId)) {
            throw new NotFoundException("Lesson not found in course");
        }
        User teacher = findUser(request.teacherId());
        if (teacher.getRole() != UserRole.TEACHER) {
            throw new BadRequestException("Teacher must have TEACHER role");
        }
        lesson.setStartTime(parseTime(request.startTime()));
        lesson.setFinishTime(parseTime(request.finishTime()));
        lesson.setDayOfWeek(parseDayOfWeek(request.dayOfWeek()));
        lesson.setTeacher(teacher);
        return toLessonResponse(lesson);
    }

    @Transactional
    public void deleteLesson(Long courseId, Long lessonId) {
        findCourse(courseId);
        Lesson lesson = findLesson(lessonId);
        if (!lesson.getCourse().getId().equals(courseId)) {
            throw new NotFoundException("Lesson not found in course");
        }
        lessonRepository.delete(lesson);
    }

    public Page<UserResponse> getTeachers(Long courseId, Pageable pageable) {
        Course course = findCourse(courseId);
        return pageList(course.getTeachers(), pageable);
    }

    @Transactional
    public void addTeacher(Long courseId, Long userId) {
        Course course = findCourse(courseId);
        User teacher = findUser(userId);
        if (teacher.getRole() != UserRole.TEACHER) {
            throw new BadRequestException("Teacher must have TEACHER role");
        }
        if (!course.getTeachers().contains(teacher)) {
            course.getTeachers().add(teacher);
        }
    }

    @Transactional
    public void removeTeacher(Long courseId, Long teacherId) {
        Course course = findCourse(courseId);
        User teacher = findUser(teacherId);
        if (!course.getTeachers().contains(teacher)) {
            throw new NotFoundException("Teacher not in course");
        }
        course.getTeachers().remove(teacher);
    }

    public Page<UserResponse> getStudents(Long courseId, Pageable pageable) {
        Course course = findCourse(courseId);
        return pageList(course.getStudents(), pageable);
    }

    @Transactional
    public void addStudent(Long courseId, Long userId) {
        Course course = findCourse(courseId);
        User student = findUser(userId);
        if (student.getRole() != UserRole.STUDENT) {
            throw new BadRequestException("Student must have STUDENT role");
        }
        if (!course.getStudents().contains(student)) {
            course.getStudents().add(student);
        }
    }

    @Transactional
    public void removeStudent(Long courseId, Long studentId) {
        Course course = findCourse(courseId);
        User student = findUser(studentId);
        if (!course.getStudents().contains(student)) {
            throw new NotFoundException("Student not in course");
        }
        course.getStudents().remove(student);
    }

    private Course findCourse(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course not found"));
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private Lesson findLesson(Long id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Lesson not found"));
    }

    private Page<UserResponse> pageList(List<User> users, Pageable pageable) {
        int start = (int) pageable.getOffset();
        if (start > users.size()) {
            start = users.size();
        }
        int end = Math.min(start + pageable.getPageSize(), users.size());
        List<UserResponse> content = new ArrayList<>();
        for (User user : users.subList(start, end)) {
            content.add(UserService.toResponse(user));
        }
        return new PageImpl<>(content, pageable, users.size());
    }

    private LocalTime parseTime(String time) {
        try {
            return LocalTime.parse(time, TIME_FORMATTER);
        } catch (DateTimeParseException | NullPointerException ex) {
            throw new BadRequestException("Incorrect time");
        }
    }

    private LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException | NullPointerException ex) {
            throw new BadRequestException("Incorrect date");
        }
    }

    private DayOfWeek parseDayOfWeek(String day) {
        try {
            return DayOfWeek.valueOf(day.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new BadRequestException("Incorrect day of week");
        }
    }

    public static CourseResponse toResponse(Course course) {
        List<UserResponse> teachers = course.getTeachers().stream()
                .map(UserService::toResponse)
                .toList();
        List<UserResponse> students = course.getStudents().stream()
                .map(UserService::toResponse)
                .toList();
        List<LessonResponse> lessons = course.getLessons().stream()
                .map(CourseService::toLessonResponse)
                .toList();
        return new CourseResponse(
                course.getId(),
                course.getName(),
                course.getStartDate().toString(),
                course.getEndDate().toString(),
                course.getDescription(),
                teachers,
                students,
                lessons
        );
    }

    public static LessonResponse toLessonResponse(Lesson lesson) {
        return new LessonResponse(
                lesson.getId(),
                lesson.getStartTime().format(TIME_FORMATTER),
                lesson.getFinishTime().format(TIME_FORMATTER),
                capitalize(lesson.getDayOfWeek().name()),
                lesson.getTeacher() != null ? UserService.toResponse(lesson.getTeacher()) : null
        );
    }

    private static String capitalize(String name) {
        return name.isEmpty() ? name
                : name.charAt(0) + name.substring(1).toLowerCase(Locale.ROOT);
    }
}
