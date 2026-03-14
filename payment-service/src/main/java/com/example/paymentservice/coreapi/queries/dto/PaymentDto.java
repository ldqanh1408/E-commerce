package com.example.paymentservice.coreapi.queries.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDto {
    private String paymentId;
    private String orderId;
    private BigDecimal amount;
    private String status;
    private String paymentMethod;
    private String transactionId;
    private Instant createdAt;
}

