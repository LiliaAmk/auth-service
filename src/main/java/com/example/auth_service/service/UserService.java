package com.example.auth_service.service;

import com.example.auth_service.model.User;
import com.example.auth_service.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    @PostConstruct
    public void init() {
        // Preload an admin
        if (repo.findByEmail("admin@example.com").isEmpty()) {
            Set<String> adminRoles = new HashSet<>(Arrays.asList("ROLE_ADMIN", "ROLE_USER"));
            User admin = new User(
                null,
                "admin@example.com",
                "adminPass",
                "saltAdmin",
                null,
                null,
                0,
                adminRoles
            );
            repo.save(admin);
        }

        // Preload a normal user
        if (repo.findByEmail("user@example.com").isEmpty()) {
            Set<String> userRoles = new HashSet<>(Arrays.asList("ROLE_USER"));
            User user = new User(
                null,
                "user@example.com",
                "userPass",
                "saltUser",
                null,
                null,
                0,
                userRoles
            );
            repo.save(user);
        }
    }

    public Optional<User> findByEmail(String email) {
        return repo.findByEmail(email);
    }

    public User save(User user) {
        return repo.save(user);
    }

    public void recordLogin(User u) {
        u.setLastLogin(LocalDateTime.now());
        u.setLoginCount(u.getLoginCount() + 1);
        repo.save(u);
    }

    public void recordLogout(User u) {
        u.setLastLogout(LocalDateTime.now());
        repo.save(u);
    }
}
