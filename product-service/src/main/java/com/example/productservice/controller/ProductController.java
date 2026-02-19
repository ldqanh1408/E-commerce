package com.example.productservice.controller;

import com.example.productservice.coreapi.queries.dto.ProductDto;
import com.example.productservice.query.service.ProductCacheService;
import com.example.productservice.query.service.ProductReadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductReadService productReadService;
    private final ProductCacheService productCacheService;

    @GetMapping("/{id}")
    public ProductDto getProduct(@PathVariable String id) {
        ProductDto cached = productCacheService.getProduct(id);
        if (cached != null) {
            return cached;
        }

        ProductDto product = productReadService.getProductDetails(id);
        if (product != null) {
            productCacheService.cacheProduct(product);
        }
        return product;
    }

    @GetMapping
    public List<ProductDto> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(required = false) String lastId,
            @RequestParam(required = false) String lastValue
    ) {
        log.debug("getProducts page={}, size={}, sortBy={}, sortOrder={}", page, size, sortBy, sortOrder);
        return productReadService.getProducts(page, size, sortBy, sortOrder, lastId, lastValue);
    }
}
