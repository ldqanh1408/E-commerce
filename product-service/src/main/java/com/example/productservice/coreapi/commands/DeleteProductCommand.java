package com.example.productservice.coreapi.commands;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class DeleteProductCommand {
    @TargetAggregateIdentifier
    String productId;
}

