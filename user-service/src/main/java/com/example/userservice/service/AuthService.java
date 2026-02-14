package com.example.userservice.service;

import com.example.userservice.dto.AuthDto;
import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthDto.AuthResponse register(AuthDto.RegisterRequest request) {
        // 1. Tạo User Entity từ Request
        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword())) // Hash pass
                .role("ROLE_CUSTOMER")
                .build();

        // 2. Lưu xuống DB
        repository.save(user);

        // 3. Tạo Token trả về luôn
        var jwtToken = jwtService.generateToken(user);
        return new AuthDto.AuthResponse(jwtToken);
    }

    public AuthDto.AuthResponse login(AuthDto.LoginRequest request) {
        // 1. Gọi "Sếp tổng" kiểm tra Username/Password
        // Nếu sai pass, dòng này sẽ ném Exception và dừng luôn
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // 2. Nếu đúng, lấy thông tin user ra
        var user = repository.findByUsername(request.getUsername())
                .orElseThrow();

        // 3. In thẻ (Token)
        var jwtToken = jwtService.generateToken(user);
        return new AuthDto.AuthResponse(jwtToken);
    }
}