package com.example.paymentapi.commands;

import lombok.Builder;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
@Builder
public class FailPaymentCommand {
    @TargetAggregateIdentifier
    String paymentId;
    String reason;
}

