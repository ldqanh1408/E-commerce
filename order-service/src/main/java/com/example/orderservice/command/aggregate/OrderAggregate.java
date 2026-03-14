package com.example.orderservice.command.aggregate;

import com.example.orderservice.coreapi.commands.CreateOrderCommand;
import com.example.orderservice.coreapi.commands.DeleteOrderCommand;
import com.example.orderservice.coreapi.commands.UpdateOrderCommand;
import com.example.orderservice.coreapi.events.OrderCreatedEvent;
import com.example.orderservice.coreapi.events.OrderDeletedEvent;
import com.example.orderservice.coreapi.events.OrderUpdatedEvent;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Aggregate
@NoArgsConstructor
public class OrderAggregate {

    @AggregateIdentifier
    private String orderId;
    private Long userId;
    private Map<String, Integer> items;
    private BigDecimal totalAmount;
    private String orderStatus;
    private String shippingAddress;
    private String reason;
    private Instant createdAt;
    private Instant updatedAt;

    @CommandHandler
    public OrderAggregate(CreateOrderCommand command) {
        OrderCreatedEvent event = new OrderCreatedEvent();
        BeanUtils.copyProperties(command, event);
        event.setCreatedAt(Instant.now());
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent event) {
        BeanUtils.copyProperties(event, this);
    }

    @CommandHandler
    public void handle(UpdateOrderCommand command) {
        OrderUpdatedEvent event = new OrderUpdatedEvent();
        BeanUtils.copyProperties(command, event);
        event.setUpdatedAt(Instant.now());
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(OrderUpdatedEvent event) {
        BeanUtils.copyProperties(event, this);
    }

    @CommandHandler
    public void handle(DeleteOrderCommand command) {
        AggregateLifecycle.apply(new OrderDeletedEvent(command.getOrderId()));
    }

    @EventSourcingHandler
    public void on(OrderDeletedEvent event) {
        AggregateLifecycle.markDeleted();
    }
}
