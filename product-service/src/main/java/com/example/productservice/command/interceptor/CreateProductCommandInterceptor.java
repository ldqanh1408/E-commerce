package com.example.productservice.command.interceptor;

import com.example.productservice.coreapi.commands.CreateProductCommand;
import com.example.productservice.query.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Interceptor: validate command trước khi gửi đến Aggregate.
 * VD: kiểm tra trùng productId khi tạo mới.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CreateProductCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private final ProductRepository productRepository;

    @Nonnull
    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(
            @Nonnull List<? extends CommandMessage<?>> messages) {

        return (index, command) -> {
            if (command.getPayloadType().equals(CreateProductCommand.class)) {
                CreateProductCommand cmd = (CreateProductCommand) command.getPayload();

                // Kiểm tra trùng SKU
                if (cmd.getSku() != null) {
                    productRepository.findBySku(cmd.getSku()).ifPresent(p -> {
                        throw new IllegalArgumentException("SKU đã tồn tại: " + cmd.getSku());
                    });
                }

                log.info("Intercepted CreateProductCommand: {}", cmd.getProductId());
            }
            return command;
        };
    }
}

