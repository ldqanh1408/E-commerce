package com.example.orderservice.coreapi.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {
    private String orderId;
    private Long userId;
    private Map<String, Integer> items;
    private BigDecimal totalAmount;
    private String orderStatus;
    private String shippingAddress;
    private Instant createdAt;
}
