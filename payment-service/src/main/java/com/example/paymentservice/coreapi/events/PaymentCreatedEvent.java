package com.example.paymentservice.coreapi.events;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class PaymentCreatedEvent {
    String paymentId;
    String orderId;
    BigDecimal amount;
    String paymentMethod;
}

