package com.example.productservice.coreapi.queries;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetProductsQuery {
    private int page;
    private int size;
    private String sortBy = "id";
    private String sortOrder = "asc";
    private String lastId; // For cursor-based pagination
    private String lastValue; // For cursor-based pagination (e.g., last price)
}
