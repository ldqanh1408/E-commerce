package com.example.paymentservice.coreapi.events;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class PaymentFailedEvent {
    String paymentId;
    String orderId;
    BigDecimal amount;
    String paymentMethod;
    String reason;
}

