package com.ecommerce.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Cấu hình security có thể tuỳ chỉnh từ application.yaml của từng service.
 *
 * Ví dụ trong application.yaml:
 * <pre>
 * core-security:
 *   public-paths:
 *     - /api/v1/auth/**
 *     - /actuator/**
 *   cors:
 *     allowed-origins:
 *       - http://localhost:3000
 *   jwt:
 *     secret-key: "your-secret-key"
 *     expiration: 86400000
 * </pre>
 */
@ConfigurationProperties(prefix = "core-security")
public class CoreSecurityProperties {

    /**
     * Danh sách các path được phép truy cập mà không cần xác thực.
     * Mặc định: /actuator/**, /api/v1/auth/**
     */
    private List<String> publicPaths = new ArrayList<>(List.of(
            "/actuator/**",
            "/api/v1/auth/**"
    ));

    /**
     * Cấu hình CORS
     */
    private CorsConfig cors = new CorsConfig();

    /**
     * Cấu hình JWT
     */
    private JwtConfig jwt = new JwtConfig();

    public List<String> getPublicPaths() {
        return publicPaths;
    }

    public void setPublicPaths(List<String> publicPaths) {
        this.publicPaths = publicPaths;
    }

    public CorsConfig getCors() {
        return cors;
    }

    public void setCors(CorsConfig cors) {
        this.cors = cors;
    }

    public JwtConfig getJwt() {
        return jwt;
    }

    public void setJwt(JwtConfig jwt) {
        this.jwt = jwt;
    }

    public static class CorsConfig {
        private List<String> allowedOrigins = List.of("*");
        private List<String> allowedMethods = List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS");
        private List<String> allowedHeaders = List.of("*");
        private boolean allowCredentials = false;

        public List<String> getAllowedOrigins() {
            return allowedOrigins;
        }

        public void setAllowedOrigins(List<String> allowedOrigins) {
            this.allowedOrigins = allowedOrigins;
        }

        public List<String> getAllowedMethods() {
            return allowedMethods;
        }

        public void setAllowedMethods(List<String> allowedMethods) {
            this.allowedMethods = allowedMethods;
        }

        public List<String> getAllowedHeaders() {
            return allowedHeaders;
        }

        public void setAllowedHeaders(List<String> allowedHeaders) {
            this.allowedHeaders = allowedHeaders;
        }

        public boolean isAllowCredentials() {
            return allowCredentials;
        }

        public void setAllowCredentials(boolean allowCredentials) {
            this.allowCredentials = allowCredentials;
        }
    }

    public static class JwtConfig {
        /**
         * Secret key dùng để ký JWT. Bắt buộc phải có.
         * Nên đặt giá trị mặc định dài và phức tạp hoặc bắt buộc cấu hình.
         */
        private String secretKey = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"; // Default key for dev

        /**
         * Thời gian hết hạn của token (ms). Mặc định 1 ngày.
         */
        private long expiration = 86400000;

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public long getExpiration() {
            return expiration;
        }

        public void setExpiration(long expiration) {
            this.expiration = expiration;
        }
    }
}
