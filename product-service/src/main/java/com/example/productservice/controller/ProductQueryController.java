package com.example.productservice.controller;

import com.example.productapi.queries.GetProductDetailQuery;
import com.example.productapi.queries.GetProductsByIdsQuery;
import com.example.productapi.queries.GetProductsQuery;
import com.example.productapi.queries.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Query Controller — chỉ chứa các endpoint ĐỌC dữ liệu (CQRS Query side).
 * Gửi query qua Axon QueryGateway → ProductProjection (QueryHandler) xử lý.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductQueryController {

    private final QueryGateway queryGateway;

    /**
     * GET /api/v1/products/{id} — Lấy 1 sản phẩm theo productId
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable String id) {
        ProductDto product = queryGateway.query(
                new GetProductDetailQuery(id),
                ResponseTypes.instanceOf(ProductDto.class)
        ).join();

        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    /**
     * GET /api/v1/products — Lấy danh sách sản phẩm (phân trang)
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseEntity<List<ProductDto>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(required = false) String lastId,
            @RequestParam(required = false) String lastValue
    ) {
        log.debug("getProducts page={}, size={}, sortBy={}, sortOrder={}", page, size, sortBy, sortOrder);

        GetProductsQuery query = new GetProductsQuery(page, size, sortBy, sortOrder, lastId, lastValue);
        List<ProductDto> products = queryGateway.query(
                query,
                ResponseTypes.multipleInstancesOf(ProductDto.class)
        ).join();

        return ResponseEntity.ok(products);
    }

    /**
     * GET /api/v1/products/batch?ids=id1,id2,id3 — Lấy nhiều sản phẩm theo danh sách ID
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/batch")
    public ResponseEntity<List<ProductDto>> getProductsByIds(@RequestParam List<String> ids) {
        List<ProductDto> products = queryGateway.query(
                new GetProductsByIdsQuery(ids),
                ResponseTypes.multipleInstancesOf(ProductDto.class)
        ).join();

        return ResponseEntity.ok(products);
    }
}
