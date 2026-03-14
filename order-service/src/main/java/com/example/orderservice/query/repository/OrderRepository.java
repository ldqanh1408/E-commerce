package com.example.orderservice.query.repository;

import com.example.orderservice.query.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {
}
