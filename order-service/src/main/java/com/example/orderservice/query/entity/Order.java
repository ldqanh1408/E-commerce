package com.example.orderservice.query.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "orders", schema = "public", uniqueConstraints = {@UniqueConstraint(name = "orders_order_id_key",
        columnNames = {"order_id"})})
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @jakarta.validation.constraints.Size(max = 255)
    @jakarta.validation.constraints.NotNull
    @Column(name = "order_id", nullable = false)
    private String orderId;

    @jakarta.validation.constraints.NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @jakarta.validation.constraints.NotNull
    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @jakarta.validation.constraints.Size(max = 50)
    @jakarta.validation.constraints.NotNull
    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @jakarta.validation.constraints.NotNull
    @Column(name = "shipping_address", nullable = false, length = Integer.MAX_VALUE)
    private String shippingAddress;

    @jakarta.validation.constraints.Size(max = 255)
    @Column(name = "reason")
    private String reason;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;


}