package com.example.productservice.coreapi.queries;

import lombok.Value;

import java.util.List;

@Value
public class GetProductsByIdsQuery {
    List<String> productIds;
}

