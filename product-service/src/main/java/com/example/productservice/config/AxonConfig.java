package com.example.productservice.config;

import com.example.productservice.command.interceptor.CreateProductCommandInterceptor;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.CommandBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AxonConfig {

    @Autowired
    public void registerInterceptors(CommandBus commandBus,
                                     CreateProductCommandInterceptor createProductInterceptor) {
        commandBus.registerDispatchInterceptor(createProductInterceptor);
    }
}

