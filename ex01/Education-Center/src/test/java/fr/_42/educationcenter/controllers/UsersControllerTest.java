package fr._42.educationcenter.controllers;

import fr._42.educationcenter.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    @Test
    public void getAllUsersTest() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getUserTest() throws Exception {
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void createUserTest() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(USER_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void updateUserTest() throws Exception {
        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(USER_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteUserTest() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isForbidden());
    }
}