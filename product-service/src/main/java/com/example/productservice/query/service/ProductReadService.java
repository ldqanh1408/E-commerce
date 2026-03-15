package com.example.productservice.query.service;

import com.example.productservice.query.entity.Product;
import com.example.productservice.query.service.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Orchestrator: cache-aside logic ONLY.
 * Đọc dữ liệu chỉ từ bảng products (không JOIN inventory).
 * Bảng products đã có cột quantity (Available) được đồng bộ bởi EventHandler.
 */
@Service
@RequiredArgsConstructor
public class ProductReadService {

    private final ProductQueryService queryService;
    private final ProductCacheService cache;
    private final ProductMapper mapper;

    // ── Public API ──────────────────────────────────────────────────

    /**
     * Returns a page of products (cache-aside).
     */
    public List<com.example.productapi.queries.dto.ProductDto> findProducts(int page, int size,
                                         String sortBy, String sortOrder,
                                         String lastId, String lastValue) {
        String sort  = defaultIfBlank(sortBy, "id");
        String order = defaultIfBlank(sortOrder, "asc");
        boolean useCursor = lastId != null && !lastId.isBlank();

        String pageKey = useCursor
                ? cache.cursorKey(lastId, lastValue, size, sort, order)
                : cache.pageKey(page, size, sort, order);

        // 1. Check page-level cache
        List<String> cachedIds = cache.getPageIds(pageKey);
        if (cachedIds != null) {
            return resolveFromCache(cachedIds);
        }

        // 2. Cache miss → query DB
        return loadFromDbAndCache(page, size, sort, order, lastId, lastValue, useCursor, pageKey);
    }

    /**
     * Returns a single product by ID (cache-aside).
     */
    public com.example.productapi.queries.dto.ProductDto findProductById(String productId) {
        // 1. Check cache
        com.example.productapi.queries.dto.ProductDto cached = cache.get(productId);
        if (cached != null) return cached;

        // 2. Cache miss → query bảng products (không JOIN inventory)
        Product product = queryService.findById(productId);
        if (product == null) return null;

        com.example.productapi.queries.dto.ProductDto dto = mapper.toDto(product);

        // 3. Populate cache
        cache.put(dto);
        return dto;
    }

    /**
     * Lấy nhiều sản phẩm theo danh sách productId (cache-aside).
     */
    public List<com.example.productapi.queries.dto.ProductDto> findProductsByIds(List<String> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyList();
        }
        return resolveFromCache(productIds);
    }

    // ── Cache MISS path ─────────────────────────────────────────────

    private List<com.example.productapi.queries.dto.ProductDto> loadFromDbAndCache(int page, int size,
                                                 String sortBy, String sortOrder,
                                                 String lastId, String lastValue,
                                                 boolean useCursor, String pageKey) {
        List<Product> products = useCursor
                ? queryService.findByKeyset(lastId, lastValue, size, sortBy, sortOrder)
                : queryService.findByOffset(page, size, sortBy, sortOrder);

        if (products.isEmpty()) {
            cache.putPageIds(pageKey, Collections.emptyList());
            return Collections.emptyList();
        }

        List<String> ids = products.stream().map(Product::getProductId).toList();
        List<com.example.productapi.queries.dto.ProductDto> dtos = mapper.toDtos(products);

        cache.putPageIds(pageKey, ids);
        cache.putAll(dtos);
        return dtos;
    }

    // ── Cache HIT path ──────────────────────────────────────────────

    private List<com.example.productapi.queries.dto.ProductDto> resolveFromCache(List<String> productIds) {
        if (productIds.isEmpty()) return Collections.emptyList();

        List<com.example.productapi.queries.dto.ProductDto> cached = cache.getAll(productIds);
        Map<String, com.example.productapi.queries.dto.ProductDto> resultMap = new LinkedHashMap<>();
        List<String> missingIds = new ArrayList<>();

        for (int i = 0; i < productIds.size(); i++) {
            com.example.productapi.queries.dto.ProductDto dto = cached.get(i);
            if (dto != null) {
                resultMap.put(productIds.get(i), dto);
            } else {
                missingIds.add(productIds.get(i));
            }
        }

        if (!missingIds.isEmpty()) {
            fillMissingFromDb(missingIds, resultMap);
        }

        return productIds.stream()
                .map(resultMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private void fillMissingFromDb(List<String> missingIds, Map<String, com.example.productapi.queries.dto.ProductDto> resultMap) {
        List<Product> products = queryService.findByIds(missingIds);
        List<com.example.productapi.queries.dto.ProductDto> dtos = mapper.toDtos(products);
        cache.putAll(dtos);
        dtos.forEach(dto -> resultMap.put(dto.getProductId(), dto));
    }

    // ── Utility ─────────────────────────────────────────────────────

    private static String defaultIfBlank(String value, String fallback) {
        return (value != null && !value.isBlank()) ? value : fallback;
    }
}
