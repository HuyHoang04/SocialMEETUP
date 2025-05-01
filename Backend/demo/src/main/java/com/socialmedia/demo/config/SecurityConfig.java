package com.socialmedia.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // Enable Spring Security's web security support
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF protection for stateless APIs (common for REST APIs)
            .csrf(csrf -> csrf.disable())
            // Configure authorization rules
            .authorizeHttpRequests(authz -> authz
                // Allow unauthenticated access to all endpoints under /api/v1/users/
                .requestMatchers("/api/v1/users/**").permitAll()
                // You might want to permit access to other public endpoints here
                // .requestMatchers("/api/v1/auth/**").permitAll()
                // Any other request must be authenticated (if you have other endpoints)
                .anyRequest().authenticated()
            )
            // Configure session management to be stateless (common for REST APIs)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Add JWT filter configuration here later if needed

        return http.build();
    }
}