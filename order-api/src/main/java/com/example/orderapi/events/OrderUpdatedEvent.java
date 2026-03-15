package com.example.orderapi.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderUpdatedEvent {
    private String orderId;
    private Map<String, Integer> items;
    private BigDecimal totalAmount;
    private String orderStatus;
    private String shippingAddress;
    private String reason;
    private Instant updatedAt;
}
