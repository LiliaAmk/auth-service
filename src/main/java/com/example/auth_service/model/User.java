package com.example.auth_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * JPA entity representing an application user.
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor                     // for JPA
@AllArgsConstructor                    // generates the 10-arg constructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String fullname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true)
    private String phonenum;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String salt;

    private LocalDateTime lastLogin;
    private LocalDateTime lastLogout;
    private int loginCount;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "role", nullable = false)
    @Builder.Default
    private Set<String> roles = new HashSet<>();

    /**
     * Minimal legacy constructor matching existing tests:
     *   (id, email, password, salt, lastLogin, lastLogout, loginCount, roles)
     */
    public User(Long id,
                String email,
                String password,
                String salt,
                LocalDateTime lastLogin,
                LocalDateTime lastLogout,
                int loginCount,
                Set<String> roles) {
        this.id = id;
        this.fullname = null;
        this.email = email;
        this.phonenum = null;
        this.password = password;
        this.salt = salt;
        this.lastLogin = lastLogin;
        this.lastLogout = lastLogout;
        this.loginCount = loginCount;
        this.roles = (roles != null) ? roles : new HashSet<>();
    }
}
