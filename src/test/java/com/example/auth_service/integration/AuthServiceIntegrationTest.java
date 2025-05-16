package com.example.auth_service.integration;

import com.example.auth_service.model.LoginResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class AuthServiceIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    void fullRegisterAndLoginFlow() {
        // 1) Register a new user
        Map<String, String> registerReq = Map.of(
            "email",    "int@dms.com",
            "password", "pwd123"
        );
        ResponseEntity<String> regResp = restTemplate.postForEntity(
            url("/auth/register"), 
            registerReq, 
            String.class
        );
        assertThat(regResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(regResp.getBody()).isEqualTo("User registered successfully.");

        // 2) Login with that user
        Map<String, String> loginReq = Map.of(
            "email",    "int@dms.com",
            "password", "pwd123"
        );
        ResponseEntity<LoginResponse> loginResp = restTemplate.postForEntity(
            url("/auth/login"), 
            loginReq, 
            LoginResponse.class
        );
        assertThat(loginResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResp.getBody()).isNotNull();
        assertThat(loginResp.getBody().getToken()).isNotBlank();
    }
}
