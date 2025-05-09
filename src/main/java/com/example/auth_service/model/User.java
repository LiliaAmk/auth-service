package com.example.auth_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String salt;

  private LocalDateTime lastLogin;
  private LocalDateTime lastLogout;
  private int loginCount;

  /**
   * Each entry here is one role, e.g. "ROLE_USER" or "ROLE_ADMIN".
   * Stored in a separate table "user_roles" under column "role".
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
    name = "user_roles",
    joinColumns = @JoinColumn(name = "user_id")
  )
  @Column(name = "role", nullable = false)
  private Set<String> roles = new HashSet<>();

}
