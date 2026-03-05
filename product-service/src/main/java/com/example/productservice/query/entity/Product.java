package com.example.productservice.query.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "products", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "products_product_id_key",
                columnNames = {"product_id"}),
        @UniqueConstraint(name = "products_sku_key",
                columnNames = {"sku"})},
        indexes = {
                @Index(name = "idx_products_price", columnList = "price"),
                @Index(name = "idx_products_name", columnList = "name"),
                @Index(name = "idx_products_created_at", columnList = "created_at"),
                @Index(name = "idx_products_price_pid", columnList = "price, product_id")
        })
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "product_id", nullable = false)
    private String productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @NotNull
    @Column(name = "price", nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @NotNull
    @Column(name = "quantity", nullable = false, columnDefinition = "integer default 0")
    private Integer quantity;

    @Size(max = 100)
    @Column(name = "sku", length = 100)
    private String sku;

    @Size(max = 500)
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "created_at")
    private Instant createdAt;


}