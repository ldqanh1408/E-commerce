package com.example.productservice.coreapi.events;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class ProductCreatedEvent {
    String productId;
    String name;
    String description;
    BigDecimal price;
    String sku;
    String imageUrl;
    Long categoryId;
    Boolean active;
}

