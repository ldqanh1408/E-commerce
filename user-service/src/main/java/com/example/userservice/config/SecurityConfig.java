package com.example.userservice.config;

import com.ecommerce.security.filter.GatewayHeaderAuthenticationFilter;
import com.ecommerce.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * SecurityConfig riêng cho user-service.
 *
 * User-service là service duy nhất cần AuthenticationProvider (để login/register),
 * nên cần override SecurityFilterChain mặc định từ core-security.
 *
 * Tuy nhiên, vẫn dùng GatewayHeaderAuthenticationFilter từ core-security
 * để đọc header X-Logged-In-User từ API Gateway cho các API khác (vd: /api/v1/users/me).
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final GatewayHeaderAuthenticationFilter gatewayHeaderAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/users/register", "/api/v1/auth/login", "/api/v1/auth/validate").permitAll()
                        .requestMatchers("/api/v1/auth/**").permitAll()  // Login/Register mở cửa
                        .requestMatchers("/actuator/**").permitAll()     // Health check
                        .anyRequest().authenticated()                    // Các API khác phải authenticated
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(gatewayHeaderAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}