package com.example.productservice.coreapi.queries.dto;


import lombok.Value;

import java.math.BigDecimal;

@Value // Immutable object (Tốt cho DTO)
public class ProductDto {
    String productId;
    String name;
    BigDecimal price;
    Integer quantity;
}
