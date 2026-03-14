package com.example.orderservice.query.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "order_items", schema = "public")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "order_db_id")
    private com.example.orderservice.query.entity.Order orderDb;

    @jakarta.validation.constraints.Size(max = 255)
    @jakarta.validation.constraints.NotNull
    @Column(name = "product_id", nullable = false)
    private String productId;

    @jakarta.validation.constraints.Size(max = 255)
    @jakarta.validation.constraints.NotNull
    @Column(name = "product_name", nullable = false)
    private String productName;

    @jakarta.validation.constraints.NotNull
    @Column(name = "price", nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @jakarta.validation.constraints.NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @jakarta.validation.constraints.NotNull
    @Column(name = "sub_total", nullable = false, precision = 19, scale = 2)
    private BigDecimal subTotal;


}