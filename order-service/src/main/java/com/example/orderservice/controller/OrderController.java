package com.example.orderservice.controller;

import com.example.orderservice.coreapi.commands.CreateOrderCommand;
import com.example.orderservice.coreapi.commands.DeleteOrderCommand;
import com.example.orderservice.coreapi.commands.UpdateOrderCommand;
import com.example.orderservice.coreapi.queries.FindAllOrdersQuery;
import com.example.orderservice.coreapi.queries.FindOrderQuery;
import com.example.orderservice.query.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    @PostMapping
    public CompletableFuture<String> createOrder(@RequestBody CreateOrderRequest request) {
        String orderId = UUID.randomUUID().toString();
        return commandGateway.send(new CreateOrderCommand(
                orderId,
                request.userId(),
                request.items(),
                request.totalAmount(),
                "CREATED",
                request.shippingAddress()
        ));
    }

    @PutMapping("/{orderId}")
    public CompletableFuture<Void> updateOrder(@PathVariable String orderId, @RequestBody UpdateOrderRequest request) {
        return commandGateway.send(new UpdateOrderCommand(
                orderId,
                request.items(),
                request.totalAmount(),
                request.orderStatus(),
                request.shippingAddress(),
                request.reason()
        ));
    }

    @DeleteMapping("/{orderId}")
    public CompletableFuture<Void> deleteOrder(@PathVariable String orderId) {
        return commandGateway.send(new DeleteOrderCommand(orderId));
    }

    @GetMapping("/{orderId}")
    public CompletableFuture<OrderDto> getOrder(@PathVariable String orderId) {
        return queryGateway.query(new FindOrderQuery(orderId), ResponseTypes.instanceOf(OrderDto.class));
    }

    @GetMapping
    public CompletableFuture<List<OrderDto>> getOrders() {
        return queryGateway.query(new FindAllOrdersQuery(), ResponseTypes.multipleInstancesOf(OrderDto.class));
    }

    // --- Request DTOs ---
    public record CreateOrderRequest(Long userId, Map<String, Integer> items, BigDecimal totalAmount, String shippingAddress) {}
    public record UpdateOrderRequest(Map<String, Integer> items, BigDecimal totalAmount, String orderStatus, String shippingAddress, String reason) {}
}
