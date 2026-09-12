package fr._42.educationcenter.controllers;

import fr._42.educationcenter.exceptions.BadRequestException;
import fr._42.educationcenter.exceptions.NotFoundException;
import fr._42.educationcenter.models.Course;
import fr._42.educationcenter.models.CourseState;
import fr._42.educationcenter.repositories.CourseRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RepositoryRestController
@Tag(name = "Courses", description = "Course operations")
public class CoursePublishController {

    private final CourseRepository courseRepository;

    public CoursePublishController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @PostMapping("/courses/{id}/publish")
    @Operation(summary = "Publish a course", description = "Publishes a DRAFT course and returns its HAL representation.")
    @ApiResponse(responseCode = "200", description = "Course published", content = @Content)
    @ApiResponse(responseCode = "400", description = "Course is already published")
    @ApiResponse(responseCode = "404", description = "Course not found")
    public ResponseEntity<PersistentEntityResource> publish(@PathVariable("id") Long id,
                                                            PersistentEntityResourceAssembler assembler) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course not found"));

        if (course.getState() == CourseState.PUBLISHED) {
            throw new BadRequestException("Course is already published");
        }

        course.setState(CourseState.PUBLISHED);
        courseRepository.save(course);

        return ResponseEntity.ok(assembler.toFullResource(course));
    }
}