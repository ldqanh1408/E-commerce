package com.example.productservice.controller;

import com.example.productservice.coreapi.queries.GetProductDetailQuery;
import com.example.productservice.coreapi.queries.dto.ProductDto;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    @Autowired
    private QueryGateway queryGateway;


    @GetMapping("/{id}")
    public CompletableFuture<ProductDto> getProduct(@PathVariable String id) {
        return queryGateway.query(
                new GetProductDetailQuery(id),
                ResponseTypes.instanceOf(ProductDto.class)
        );
    }
}
