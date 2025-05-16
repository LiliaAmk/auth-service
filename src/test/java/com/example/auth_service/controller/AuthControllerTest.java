package com.example.auth_service.controller;

import com.example.auth_service.model.User;
import com.example.auth_service.service.UserService;
import com.example.auth_service.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("POST /auth/register → 200 OK when new user")
    void whenRegister_then200() throws Exception {
        when(userService.findByEmail("new@dms.com"))
            .thenReturn(Optional.empty());

        String payload = """
            {
              "email":"new@dms.com",
              "password":"secret"
            }
            """;

        mvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
           .andExpect(status().isOk())
           .andExpect(content().string("User registered successfully."));

        verify(userService).save(ArgumentMatchers.any(User.class));
    }

    @Test
    @DisplayName("POST /auth/login → 200 OK + token when valid credentials")
    void whenLoginValid_then200AndToken() throws Exception {
        User user = new User(
            1L,
            "alice@dms.com",
            "pass",
            "someSalt",
            null,
            null,
            0,
            Set.of("ROLE_USER")
        );
        when(userService.findByEmail("alice@dms.com"))
            .thenReturn(Optional.of(user));
        doNothing().when(userService).recordLogin(user);
        when(jwtUtil.generateToken(user))
            .thenReturn("fake-jwt-token");

        String payload = """
            {
              "email":"alice@dms.com",
              "password":"pass"
            }
            """;

        mvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$.token").value("fake-jwt-token"));

        verify(userService).recordLogin(user);
    }

    @Test
    @DisplayName("POST /auth/login → 401 when bad credentials")
    void whenLoginInvalid_then401() throws Exception {
        when(userService.findByEmail("bob@dms.com"))
            .thenReturn(Optional.empty());

        String payload = """
            {
              "email":"bob@dms.com",
              "password":"wrong"
            }
            """;

        mvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
           .andExpect(status().isUnauthorized());
    }
}
