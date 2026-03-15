package com.example.productapi.queries;

import lombok.Value;

import java.util.List;

@Value
public class GetProductsByIdsQuery {
    List<String> productIds;
}

