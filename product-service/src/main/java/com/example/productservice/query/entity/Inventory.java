package com.example.productservice.query.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "inventory", schema = "public", uniqueConstraints = {@UniqueConstraint(name = "inventory_product_id_key",
        columnNames = {"product_id"})})
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "product_id", nullable = false)
    private String productId;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull
    @Column(name = "reserved", nullable = false)
    private Integer reserved;

    @Column(name = "updated_at")
    private Instant updatedAt;


}