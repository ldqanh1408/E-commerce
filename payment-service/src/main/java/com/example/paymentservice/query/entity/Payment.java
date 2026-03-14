package com.example.paymentservice.query.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "payments", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "payments_payment_id_key",
                columnNames = {"payment_id"}),
        @UniqueConstraint(name = "payments_order_id_key",
                columnNames = {"order_id"})})
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @jakarta.validation.constraints.Size(max = 255)
    @jakarta.validation.constraints.NotNull
    @Column(name = "payment_id", nullable = false)
    private String paymentId;

    @jakarta.validation.constraints.Size(max = 255)
    @jakarta.validation.constraints.NotNull
    @Column(name = "order_id", nullable = false)
    private String orderId;

    @jakarta.validation.constraints.NotNull
    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @jakarta.validation.constraints.Size(max = 50)
    @jakarta.validation.constraints.NotNull
    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @jakarta.validation.constraints.Size(max = 50)
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @jakarta.validation.constraints.Size(max = 255)
    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "created_at")
    private Instant createdAt;


}