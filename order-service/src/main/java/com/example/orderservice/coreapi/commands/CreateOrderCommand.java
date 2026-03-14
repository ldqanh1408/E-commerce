package com.example.orderservice.coreapi.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderCommand {
    @TargetAggregateIdentifier
    private String orderId;
    private Long userId;
    private Map<String, Integer> items; // ProductId -> Quantity
    private BigDecimal totalAmount;
    private String orderStatus;
    private String shippingAddress;
}
