package com.ecommerce.security.axon;

import org.axonframework.commandhandling.CommandBus;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AutoConfiguration
@ConditionalOnClass(CommandBus.class)
public class AxonSecurityConfig {

    @Bean
    @ConditionalOnMissingBean
    SecurityDispatchInterceptor securityDispatchInterceptor() {
        return new SecurityDispatchInterceptor();
    }

    /**
     * Separate inner config that depends on CommandBus bean being available,
     * avoiding circular dependency with the interceptor bean above.
     */
    @Configuration
    @ConditionalOnBean(CommandBus.class)
    static class AxonInterceptorRegistrar {

        @Bean
        CommandBusInterceptorConfigurer commandBusInterceptorConfigurer(
                CommandBus commandBus, SecurityDispatchInterceptor interceptor) {
            // Đăng ký bộ đánh chặn này vào CommandBus của service sử dụng thư viện
            commandBus.registerDispatchInterceptor(interceptor);
            return new CommandBusInterceptorConfigurer();
        }
    }

    /**
     * Marker class to represent the registration as a bean.
     */
    static class CommandBusInterceptorConfigurer {
    }
}
