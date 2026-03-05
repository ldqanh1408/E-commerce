package com.ecommerce.security.axon;

import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.function.BiFunction;

public class SecurityDispatchInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(List<? extends CommandMessage<?>> messages) {
        return (index, command) -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()) {
                String userId = (String) authentication.getPrincipal();
                String role = authentication.getAuthorities().iterator().next().getAuthority();

                return command.andMetaData(java.util.Map.of(
                        "userId", userId,
                        "userRole", role
                ));
            }
            return command;
        };
    }
}