package com.example.productapi.events;

import lombok.Value;

@Value
public class ProductDeletedEvent {
    String productId;
}

