package com.example.productapi.queries.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private String productId;
    private String name;
    private BigDecimal price;
    private Integer availableStock; // = quantity trong bảng products (đã trừ reserved)
    private String imageUrl;
    private String description;
    private String sku;
    private String categoryName;
}
