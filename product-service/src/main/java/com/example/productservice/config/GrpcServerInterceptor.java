package com.example.productservice.config;

import com.ecommerce.security.util.JwtUtil;
import io.grpc.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

@Slf4j
@GrpcGlobalServerInterceptor
@RequiredArgsConstructor
public class GrpcServerInterceptor implements ServerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        String token = headers.get(Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER));
        Authentication authentication = null;

        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);
            try {
                jwtUtil.validateToken(jwt);
                String username = jwtUtil.extractUsername(jwt);

                UserDetails userDetails = new User(username, "", Collections.emptyList());
                authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                
                log.debug("gRPC Auth Success: {}", username);

            } catch (Exception e) {
                log.warn("gRPC Auth Failed: {}", e.getMessage());
                call.close(Status.UNAUTHENTICATED.withDescription("Invalid token"), headers);
                return new ServerCall.Listener<>() {};
            }
        }

        final Authentication finalAuth = authentication;

        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(next.startCall(call, headers)) {
            @Override
            public void onMessage(ReqT message) {
                if (finalAuth != null) {
                    SecurityContextHolder.getContext().setAuthentication(finalAuth);
                }
                try {
                    super.onMessage(message);
                } finally {
                    if (finalAuth != null) {
                        SecurityContextHolder.clearContext();
                    }
                }
            }

            @Override
            public void onHalfClose() {
                if (finalAuth != null) {
                    SecurityContextHolder.getContext().setAuthentication(finalAuth);
                }
                try {
                    super.onHalfClose();
                } finally {
                    if (finalAuth != null) {
                        SecurityContextHolder.clearContext();
                    }
                }
            }
        };
    }
}
