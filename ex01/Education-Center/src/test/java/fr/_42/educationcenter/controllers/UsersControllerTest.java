package fr._42.educationcenter.controllers;

import fr._42.educationcenter.dto.UserRequest;
import fr._42.educationcenter.dto.UserResponse;
import fr._42.educationcenter.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private static final String USER_JSON = """
            {
                "firstName": "Best",
                "lastName": "Teacher",
                "role": "TEACHER",
                "login": "bteacher",
                "password": "teacher123"
            }
            """;

    @BeforeEach
    public void setUp() {
        when(userService.createUser(any(UserRequest.class)))
                .thenReturn(new UserResponse(
                        1L,
                        "Best",
                        "Teacher",
                        "TEACHER",
                        "bteacher"
                ));

        when(userService.getUser(1L))
                .thenReturn(new UserResponse(
                        1L,
                        "Best",
                        "Teacher",
                        "TEACHER",
                        "bteacher"
                ));

        when(userService.updateUser(eq(1L), any(UserRequest.class)))
                .thenReturn(new UserResponse(
                        1L,
                        "New",
                        "Name",
                        "ADMINISTRATOR",
                        "newname"
                ));
    }

    @Test
    public void createUserTest() throws Exception {
        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(USER_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Best"))
                .andExpect(jsonPath("$.lastName").value("Teacher"))
                .andExpect(jsonPath("$.role").value("TEACHER"))
                .andExpect(jsonPath("$.login").value("bteacher"));
    }

    @Test
    public void getUserTest() throws Exception {
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Best"))
                .andExpect(jsonPath("$.lastName").value("Teacher"))
                .andExpect(jsonPath("$.role").value("TEACHER"))
                .andExpect(jsonPath("$.login").value("bteacher"));
    }

    @Test
    public void updateUserTest() throws Exception {
        mockMvc.perform(
                        put("/users/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(USER_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("New"))
                .andExpect(jsonPath("$.lastName").value("Name"))
                .andExpect(jsonPath("$.role").value("ADMINISTRATOR"))
                .andExpect(jsonPath("$.login").value("newname"));
    }

    @Test
    public void getUpdatedUserTest() throws Exception {
        when(userService.getUser(1L))
                .thenReturn(new UserResponse(
                        1L,
                        "New",
                        "Name",
                        "ADMINISTRATOR",
                        "newname"
                ));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("New"))
                .andExpect(jsonPath("$.lastName").value("Name"))
                .andExpect(jsonPath("$.role").value("ADMINISTRATOR"))
                .andExpect(jsonPath("$.login").value("newname"));
    }

    @Test
    public void deleteUserTest() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }
}
