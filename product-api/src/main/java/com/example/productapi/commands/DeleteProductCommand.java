package com.example.productapi.commands;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class DeleteProductCommand {
    @TargetAggregateIdentifier
    String productId;
}

