// src/main/java/com/example/auth_service/config/SecurityConfig.java
package com.example.auth_service.config;

import com.example.auth_service.filter.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
          // Enable CORS with default settings (no need for .and())
          .cors(Customizer.withDefaults())

          // Disable CSRF for stateless API
          .csrf(csrf -> csrf.disable())

          // Configure URL access rules
          .authorizeHttpRequests(auth -> auth
              // Allow CORS preflight
              .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

              // Open Actuator health & info
              .requestMatchers("/actuator/health", "/actuator/info").permitAll()

              // Open authentication endpoints, H2-console, error path
              .requestMatchers("/auth/**", "/h2-console/**", "/error").permitAll()

              // All other requests require authentication
              .anyRequest().authenticated()
          )

          // Stateless session management
          .sessionManagement(sm ->
              sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
          )
          

          // Allow framing (for H2-console)
          .headers(headers ->
              headers.frameOptions(frame -> frame.disable())
          )

          // Add JWT filter before UsernamePasswordAuthenticationFilter
          .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }
}
