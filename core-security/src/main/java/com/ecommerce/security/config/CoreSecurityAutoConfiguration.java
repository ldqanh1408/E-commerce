package com.ecommerce.security.config;

import com.ecommerce.security.filter.GatewayHeaderAuthenticationFilter;
import com.ecommerce.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Cấu hình Security chung cho tất cả downstream microservices.
 *
 * Các service chỉ cần thêm dependency core-security vào pom.xml là tự động có:
 * - GatewayHeaderAuthenticationFilter: đọc header từ API Gateway
 * - SecurityFilterChain: cấu hình stateless, disable CSRF, CORS
 * - Method-level security (@PreAuthorize, @Secured, v.v.)
 *
 * Nếu service muốn tự định nghĩa SecurityFilterChain riêng (ví dụ user-service),
 * chỉ cần tạo Bean SecurityFilterChain → @ConditionalOnMissingBean sẽ bỏ qua cái mặc định.
 */
@AutoConfiguration
@EnableConfigurationProperties(CoreSecurityProperties.class)
@RequiredArgsConstructor
@Slf4j
public class CoreSecurityAutoConfiguration {

    private final CoreSecurityProperties properties;

    /**
     * Bean JwtUtil - Utilities for JWT.
     * Use @ConditionalOnMissingBean so services can override if needed.
     * Available for both Servlet and Reactive apps.
     */
    @Bean
    @ConditionalOnMissingBean
    public JwtUtil jwtUtil() {
        return new JwtUtil(properties);
    }

    /**
     * Configuration specific for Servlet web applications.
     */
    @Configuration
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @EnableWebSecurity
    @EnableMethodSecurity  // Hỗ trợ @PreAuthorize("hasRole('ADMIN')"), @Secured, v.v.
    @RequiredArgsConstructor
    static class ServletSecurityConfig {

        private final CoreSecurityProperties properties;

        /**
         * Bean GatewayHeaderAuthenticationFilter - đọc header từ API Gateway.
         * Nếu service đã tự định nghĩa, sẽ không tạo lại.
         */
        @Bean
        @ConditionalOnMissingBean
        public GatewayHeaderAuthenticationFilter gatewayHeaderAuthenticationFilter() {
            log.info(">>> [core-security] Registering GatewayHeaderAuthenticationFilter");
            return new GatewayHeaderAuthenticationFilter();
        }

        /**
         * Cấu hình SecurityFilterChain mặc định cho downstream services.
         * - Disable CSRF (stateless API)
         * - Stateless session (không dùng HttpSession)
         * - Cho phép public paths không cần auth
         * - Mọi request khác phải authenticated
         * - Thêm GatewayHeaderAuthenticationFilter trước UsernamePasswordAuthenticationFilter
         *
         * Nếu service muốn override (như user-service), chỉ cần tạo Bean SecurityFilterChain riêng.
         */
        @Bean
        @ConditionalOnMissingBean(SecurityFilterChain.class)
        public SecurityFilterChain coreSecurityFilterChain(HttpSecurity http,
                                                           GatewayHeaderAuthenticationFilter gatewayFilter,
                                                           CorsConfigurationSource corsConfigurationSource) throws Exception {
            String[] publicPaths = properties.getPublicPaths().toArray(new String[0]);

            log.info(">>> [core-security] Configuring SecurityFilterChain with public paths: {}",
                    properties.getPublicPaths());

            http
                    // 1. Disable CSRF - API stateless không cần CSRF token
                    .csrf(AbstractHttpConfigurer::disable)

                    // 2. Cấu hình CORS
                    .cors(cors -> cors.configurationSource(corsConfigurationSource));

            // 3. Cấu hình Authorization
            http.authorizeHttpRequests(auth -> auth
                    .requestMatchers(publicPaths).permitAll()    // Các path công khai
                    .anyRequest().authenticated()                 // Còn lại phải có authentication
            );

            // 4. Stateless - Không tạo HttpSession
            http.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

            // 5. Thêm Gateway Filter trước UsernamePasswordAuthenticationFilter
            http.addFilterBefore(gatewayFilter, UsernamePasswordAuthenticationFilter.class);

            return http.build();
        }

        /**
         * Cấu hình CORS dựa trên properties.
         */
        @Bean
        @ConditionalOnMissingBean(CorsConfigurationSource.class)
        public CorsConfigurationSource corsConfigurationSource() {
            log.info(">>> [core-security] Registering CorsConfigurationSource with origins: {}", properties.getCors().getAllowedOrigins());
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(properties.getCors().getAllowedOrigins());
            configuration.setAllowedMethods(properties.getCors().getAllowedMethods());
            configuration.setAllowedHeaders(properties.getCors().getAllowedHeaders());
            configuration.setAllowCredentials(properties.getCors().isAllowCredentials());

            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", configuration);
            return source;
        }
    }

    /**
     * Cấu hình Security cho Reactive applications (Ví dụ: API Gateway).
     * Chỉ kích hoạt khi ứng dụng là Reactive (WebFlux).
     */
    @Configuration
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    @EnableWebFluxSecurity
    @RequiredArgsConstructor
    static class ReactiveSecurityConfig {

        private final CoreSecurityProperties properties;

        @Bean
        @ConditionalOnMissingBean
        public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
            return http
                    // Tắt CSRF vì API Gateway đóng vai trò proxy stateless
                    .csrf(ServerHttpSecurity.CsrfSpec::disable)
                    .authorizeExchange(exchange -> exchange
                            .pathMatchers(properties.getPublicPaths().toArray(new String[0])).permitAll()
                            .pathMatchers("/api/v1/auth/**", "/eureka/**").permitAll() // Luôn mở các path này
                            .anyExchange().permitAll() // Gateway cho phép tất cả đi qua, việc check token sẽ do Filter xử lý
                    )
                    .build();
        }
    }
}
