package com.example.userservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // 2. Kiểm tra xem Header có hợp lệ không (phải bắt đầu bằng "Bearer ")
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Nếu không có token hoặc sai định dạng, cho request đi tiếp
            // (để các filter phía sau hoặc SecurityConfig quyết định chặn hay không)
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Cắt chuỗi để lấy Token (Bỏ 7 ký tự đầu là "Bearer ")
        jwt = authHeader.substring(7);

        // 4. Trích xuất username từ Token
        username = jwtService.extractUsername(jwt);

        // 5. Nếu có username và chưa được xác thực trong SecurityContext hiện tại
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Lấy thông tin User đầy đủ từ Database
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 6. Kiểm tra Token có hợp lệ với User này không
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // 7. Tạo đối tượng Authentication
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 8. LƯU VÀO SECURITY CONTEXT -> Đánh dấu là "Đã đăng nhập"
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 9. Cho phép request đi tiếp đến các filter khác hoặc Controller
        filterChain.doFilter(request, response);
    }
}