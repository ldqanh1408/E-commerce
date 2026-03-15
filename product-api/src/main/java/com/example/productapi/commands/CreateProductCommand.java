package com.example.productapi.commands;

import lombok.Builder;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;

@Value
@Builder
public class CreateProductCommand {
    @TargetAggregateIdentifier
    String productId;
    String name;
    String description;
    BigDecimal price;
    String sku;
    String imageUrl;
    Long categoryId;
    Boolean active;
}

