// src/main/java/com/example/auth_service/controller/AuthController.java
package com.example.auth_service.controller;

import com.example.auth_service.model.LoginRequest;
import com.example.auth_service.model.LoginResponse;
import com.example.auth_service.model.User;
import com.example.auth_service.service.UserService;
import com.example.auth_service.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "*")  
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> opt = userService.findByEmail(loginRequest.getEmail());
        if (opt.isPresent() && opt.get().getPassword().equals(loginRequest.getPassword())) {
            User user = opt.get();
            userService.recordLogin(user);
            String token = jwtUtil.generateToken(user);
            return ResponseEntity.ok(new LoginResponse(token));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
            // 1) generate and set a real salt
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
}
