package com.example.productservice.coreapi.events;

import lombok.Value;

@Value
public class ProductDeletedEvent {
    String productId;
}

