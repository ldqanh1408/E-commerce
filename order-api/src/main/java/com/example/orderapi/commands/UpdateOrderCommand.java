package com.example.orderapi.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderCommand {
    @TargetAggregateIdentifier
    private String orderId;
    private Map<String, Integer> items;
    private BigDecimal totalAmount;
    private String orderStatus;
    private String shippingAddress;
    private String reason;
}
