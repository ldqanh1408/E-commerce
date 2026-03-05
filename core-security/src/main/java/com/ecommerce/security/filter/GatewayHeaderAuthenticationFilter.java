package com.ecommerce.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Filter chung cho tất cả downstream services.
 * Đọc header từ API Gateway (X-Logged-In-User, X-User-Role)
 * và thiết lập SecurityContext cho Spring Security.
 *
 * Luồng hoạt động:
 * 1. Client gửi request kèm JWT Token đến API Gateway
 * 2. API Gateway validate JWT, extract username/role, gắn vào Header
 * 3. Filter này đọc Header và đưa vào SecurityContext
 * => Các service chỉ cần dùng @AuthenticationPrincipal hoặc SecurityContextHolder
 */
@Slf4j
public class GatewayHeaderAuthenticationFilter extends OncePerRequestFilter {

    public static final String HEADER_USERNAME = "X-Logged-In-User";
    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_USER_ROLE = "X-User-Role";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String username = request.getHeader(HEADER_USERNAME);
        String userId = request.getHeader(HEADER_USER_ID);
        String role = request.getHeader(HEADER_USER_ROLE);

        // Nếu có thông tin user từ Gateway, thiết lập vào SecurityContext
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Tạo danh sách quyền (authorities)
            List<SimpleGrantedAuthority> authorities = Collections.emptyList();
            if (role != null && !role.isBlank()) {
                // Đảm bảo role có prefix ROLE_ để Spring Security nhận diện
                String normalizedRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                authorities = List.of(new SimpleGrantedAuthority(normalizedRole));
            }

            // Tạo Authentication token
            // Principal = username, Credentials = null (không cần password vì Gateway đã xác thực)
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Nạp vào SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("Authenticated user '{}' with role '{}' from Gateway headers", username, role);
        }

        // Cho phép request tiếp tục đi xuống
        filterChain.doFilter(request, response);
    }
}

