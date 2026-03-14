package com.example.orderservice.coreapi.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDeletedEvent {
    private String orderId;
}
