package com.example.orderservice.query.service;

import com.example.orderservice.query.entity.Order;
import com.example.orderservice.query.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public void save(Order order) {
        orderRepository.save(order);
    }

    public void deleteById(String orderId) {
        orderRepository.deleteById(orderId);
    }

    public Optional<Order> findById(String orderId) {
        return orderRepository.findById(orderId);
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }
}
