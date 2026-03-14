package com.example.paymentservice.coreapi.commands;

import lombok.Builder;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;

@Value
@Builder
public class CreatePaymentCommand {
    @TargetAggregateIdentifier
    String paymentId;
    String orderId;
    BigDecimal amount;
    String paymentMethod;
}

