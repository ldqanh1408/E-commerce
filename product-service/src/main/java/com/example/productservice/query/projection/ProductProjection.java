package com.example.productservice.query.projection;

import com.example.productapi.queries.GetProductDetailQuery;
import com.example.productapi.queries.GetProductsByIdsQuery;
import com.example.productapi.queries.GetProductsQuery;
import com.example.productservice.query.service.ProductReadService;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductProjection {

    private final ProductReadService productReadService;

    @QueryHandler
    public com.example.productapi.queries.dto.ProductDto handle(GetProductDetailQuery query) {
        return productReadService.findProductById(query.getProductId());
    }

    @QueryHandler
    public List<com.example.productapi.queries.dto.ProductDto> handle(GetProductsQuery query) {
        return productReadService.findProducts(
                query.getPage(), query.getSize(),
                query.getSortBy(), query.getSortOrder(),
                query.getLastId(), query.getLastValue()
        );
    }

    @QueryHandler
    public List<com.example.productapi.queries.dto.ProductDto> handle(GetProductsByIdsQuery query) {
        return productReadService.findProductsByIds(query.getProductIds());
    }
}