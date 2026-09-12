package fr._42.educationcenter.controllers;

import fr._42.educationcenter.models.Course;
import fr._42.educationcenter.models.CourseState;
import fr._42.educationcenter.repositories.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.restdocs.RestDocumentationExtension;

import java.time.LocalDate;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(RestDocumentationExtension.class)
public class CoursePublishControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    private Course draftCourse;

    @BeforeEach
    void setUp(WebApplicationContext context, RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .apply(springSecurity())
                .build();

        Course course = new Course();
        course.setName("Unit Test Course");
        course.setStartDate(LocalDate.of(2026, 9, 1));
        course.setEndDate(LocalDate.of(2026, 12, 20));
        course.setDescription("Course used by the publishing unit test.");
        course.setState(CourseState.DRAFT);
        draftCourse = courseRepository.save(course);
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATOR")
    void publishCourseShouldPublishAndReturnHalResource() throws Exception {
        mockMvc.perform(post("/courses/{id}/publish", draftCourse.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("PUBLISHED"))
                .andExpect(jsonPath("$._links.publish").doesNotExist())
                .andDo(document("course-publish",
                        pathParameters(parameterWithName("id").description("Course identifier")),
                        responseFields(
                                subsectionWithPath("_links").description("Resource links"),
                                fieldWithPath("name").description("Course name"),
                                fieldWithPath("description").description("Course description"),
                                fieldWithPath("startDate").description("Course start date"),
                                fieldWithPath("endDate").description("Course end date"),
                                fieldWithPath("state").description("Course state")
                        )));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATOR")
    void publishAlreadyPublishedCourseShouldReturnBadRequest() throws Exception {
        Course published = new Course();
        published.setName("Already Published Course");
        published.setStartDate(LocalDate.of(2026, 9, 1));
        published.setEndDate(LocalDate.of(2026, 12, 20));
        published.setState(CourseState.PUBLISHED);
        Course saved = courseRepository.save(published);

        mockMvc.perform(post("/courses/{id}/publish", saved.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.status").value(400))
                .andExpect(jsonPath("$.error.message").value("Course is already published"));
    }

    @Test
    void publishCourseWithoutAuthenticationShouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/courses/{id}/publish", draftCourse.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}