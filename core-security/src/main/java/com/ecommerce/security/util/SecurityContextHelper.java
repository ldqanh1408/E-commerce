package com.ecommerce.security.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.Optional;

/**
 * Utility class tiện ích để lấy thông tin user từ SecurityContext.
 * Dùng trong Controller hoặc Service của bất kỳ microservice nào.
 *
 * Ví dụ sử dụng:
 * <pre>
 * String username = SecurityContextHelper.getCurrentUsername()
 *     .orElseThrow(() -> new RuntimeException("User not authenticated"));
 *
 * boolean isAdmin = SecurityContextHelper.hasRole("ADMIN");
 * </pre>
 */
public final class SecurityContextHelper {

    private SecurityContextHelper() {
        // Utility class, không cho phép tạo instance
    }

    /**
     * Lấy username của user đang đăng nhập từ SecurityContext.
     */
    public static Optional<String> getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        return Optional.ofNullable(authentication.getName());
    }

    /**
     * Lấy Authentication object hiện tại.
     */
    public static Optional<Authentication> getCurrentAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * Lấy danh sách authorities/roles của user hiện tại.
     */
    public static Collection<? extends GrantedAuthority> getCurrentAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return java.util.Collections.emptyList();
        }
        return authentication.getAuthorities();
    }

    /**
     * Kiểm tra user hiện tại có role cụ thể không.
     * Tự động thêm prefix "ROLE_" nếu chưa có.
     *
     * @param role Ví dụ: "ADMIN" hoặc "ROLE_ADMIN"
     */
    public static boolean hasRole(String role) {
        String normalizedRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return getCurrentAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(normalizedRole));
    }

    /**
     * Kiểm tra user hiện tại đã authenticated chưa.
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal());
    }
}

