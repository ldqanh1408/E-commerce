package com.example.productservice.controller;

import com.example.productservice.coreapi.queries.dto.ProductDto;
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

    @GetMapping("/{id}")
    public ProductDto getProduct(@PathVariable String id) {
        return productReadService.findProductById(id);
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
        return productReadService.findProducts(page, size, sortBy, sortOrder, lastId, lastValue);
    }
}
