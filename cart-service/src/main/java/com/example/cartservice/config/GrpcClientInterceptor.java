package com.example.cartservice.config;

import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.interceptor.GrpcGlobalClientInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@Slf4j
@Configuration
@GrpcGlobalClientInterceptor
public class GrpcClientInterceptor implements ClientInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final Metadata.Key<String> AUTH_METADATA_KEY = Metadata.Key.of(AUTHORIZATION_HEADER, Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method,
            CallOptions callOptions,
            Channel next) {

        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                // Cố gắng lấy Authorization header từ HTTP request context
                try {
                    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                    if (attributes != null) {
                        String authToken = attributes.getRequest().getHeader(AUTHORIZATION_HEADER);
                        if (authToken != null && !authToken.isEmpty()) {
                            log.info("Forwarding Authorization header to gRPC call");
                            headers.put(AUTH_METADATA_KEY, authToken);
                        } else {
                            log.warn("Authorization header is missing in the incoming request.");
                        }
                    }
                } catch (Exception e) {
                    log.error("Error while trying to forward Authorization header", e);
                }

                super.start(responseListener, headers);
            }
        };
    }
}
