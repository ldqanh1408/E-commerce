package com.example.orderservice.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private String orderId;
    private Long userId;
    private Map<String, Integer> items;
    private BigDecimal totalAmount;
    private String status;
    private String shippingAddress;
    private String reason;
    private Instant createdAt;
    private Instant updatedAt;
}
