package com.example.paymentapi.events;

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

