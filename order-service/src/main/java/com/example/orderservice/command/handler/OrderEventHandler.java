package com.example.orderservice.command.handler;

import com.example.orderservice.coreapi.events.OrderCreatedEvent;
import com.example.orderservice.coreapi.events.OrderDeletedEvent;
import com.example.orderservice.coreapi.events.OrderUpdatedEvent;
import com.example.orderservice.query.entity.Order;
import com.example.orderservice.query.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventHandler {

    private final OrderService orderService;

    @EventHandler
    public void on(OrderCreatedEvent event) {
        log.info("Handling OrderCreatedEvent: {}", event.getOrderId());
        Order order = new Order();
        BeanUtils.copyProperties(event, order);
        order.setStatus(event.getOrderStatus());
        orderService.save(order);
    }

    @EventHandler
    public void on(OrderUpdatedEvent event) {
        log.info("Handling OrderUpdatedEvent: {}", event.getOrderId());
        orderService.findById(event.getOrderId()).ifPresent(order -> {
            BeanUtils.copyProperties(event, order);
            order.setStatus(event.getOrderStatus());
            orderService.save(order);
        });
    }

    @EventHandler
    public void on(OrderDeletedEvent event) {
        log.info("Handling OrderDeletedEvent: {}", event.getOrderId());
        orderService.deleteById(event.getOrderId());
    }
}
