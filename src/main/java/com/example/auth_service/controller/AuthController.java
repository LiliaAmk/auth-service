// src/main/java/com/example/auth_service/controller/AuthController.java
package com.example.auth_service.controller;

import com.example.auth_service.model.LoginRequest;
import com.example.auth_service.model.LoginResponse;
import com.example.auth_service.model.User;
import com.example.auth_service.service.UserService;
import com.example.auth_service.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        var opt = userService.findByEmail(loginRequest.getEmail());
        if (opt.isPresent() && opt.get().getPassword().equals(loginRequest.getPassword())) {
            var token = jwtUtil.generateToken(opt.get());
            return ResponseEntity.ok(new LoginResponse(token));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/admin/create-user")
    public ResponseEntity<?> adminCreateUser(
        @RequestHeader("Authorization") String authHeader,
        @RequestBody CreateUserDto dto
    ) {
        if (!authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing token");
        }
        if (userService.findByEmail(dto.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already registered.");
        }

        // Generate a new salt for hashing
        String salt = UUID.randomUUID().toString();
        User user = new User();
        user.setFullname(dto.getFullname());
        user.setEmail(dto.getEmail());
        user.setPhonenum(dto.getPhonenum());
        user.setPassword(dto.getPassword());
        user.setSalt(salt);
        user.setRoles(dto.getRoles());

        userService.save(user);
        return ResponseEntity.ok("User created successfully by admin.");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        String salt = UUID.randomUUID().toString();
        user.setSalt(salt);
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already registered.");
        }
        userService.save(user);
        return ResponseEntity.ok("User registered successfully.");
    }

    @GetMapping("/secure")
    public ResponseEntity<String> secureEndpoint(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String email = jwtUtil.extractEmail(authHeader.substring(7));
            return ResponseEntity.ok("🔐 Hello, " + email + "! You accessed a protected route.");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No token provided.");
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<String> adminOnlyEndpoint() {
        return ResponseEntity.ok("✅ You are an ADMIN!");
    }

    @Data
    static class CreateUserDto {
        private String fullname;
        private String email;
        private String phonenum;
        private String password;
        private java.util.Set<String> roles;
    }
}
