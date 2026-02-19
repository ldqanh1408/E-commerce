package com.example.productservice.query.projection;

import com.example.productservice.coreapi.queries.GetProductDetailQuery;
import com.example.productservice.coreapi.queries.GetProductsQuery;
import com.example.productservice.coreapi.queries.dto.ProductDto;
import com.example.productservice.query.service.ProductCacheService;
import com.example.productservice.query.service.ProductReadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductProjection {

    private final ProductReadService productReadService;
    private final ProductCacheService productCacheService;

    @QueryHandler
    public ProductDto handle(GetProductDetailQuery query) {
        ProductDto cached = productCacheService.getProduct(query.getProductId());
        if (cached != null) {
            return cached;
        }

        ProductDto product = productReadService.getProductDetails(query.getProductId());
        if (product != null) {
            productCacheService.cacheProduct(product);
        }
        return product;
    }

    @QueryHandler
    public List<ProductDto> handle(GetProductsQuery query) {
        return productReadService.getProducts(
                query.getPage(), query.getSize(),
                query.getSortBy(), query.getSortOrder(),
                query.getLastId(), query.getLastValue()
        );
    }
}