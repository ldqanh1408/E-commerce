package com.example.productservice.query.service;

import com.example.productservice.coreapi.queries.dto.ProductDto;
import com.example.productservice.query.entity.Inventory;
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
 * Delegates DB queries to ProductQueryService / InventoryQueryService,
 * mapping to ProductMapper, caching to ProductCacheService.
 */
@Service
@RequiredArgsConstructor
public class ProductReadService {

    private final ProductQueryService queryService;
    private final InventoryQueryService inventoryService;
    private final ProductCacheService cache;
    private final ProductMapper mapper;

    // ── Public API ──────────────────────────────────────────────────

    /**
     * Returns a page of products (cache-aside).
     * Supports offset-based and cursor-based (keyset) pagination.
     */
    public List<ProductDto> findProducts(int page, int size,
                                         String sortBy, String sortOrder,
                                         String lastId, String lastValue) {
        String sort  = defaultIfBlank(sortBy, "id");
        String order = defaultIfBlank(sortOrder, "asc");
        boolean useCursor = lastId != null && !lastId.isBlank();

        String pageKey = useCursor
                ? cache.cursorKey(lastId, lastValue, size, sort, order)
                : cache.pageKey(page, size, sort, order);

        // 1. Check page-level cache (list of IDs)
        List<String> cachedIds = cache.getPageIds(pageKey);
        if (cachedIds != null) {
            return resolveFromCache(cachedIds);
        }

        // 2. Cache miss → query DB, then populate cache
        return loadFromDbAndCache(page, size, sort, order, lastId, lastValue, useCursor, pageKey);
    }

    /**
     * Returns a single product by ID (cache-aside).
     */
    public ProductDto findProductById(String productId) {
        // 1. Check product-level cache
        ProductDto cached = cache.get(productId);
        if (cached != null) return cached;

        // 2. Cache miss → query DB
        Product product = queryService.findById(productId);
        if (product == null) return null;

        Inventory inventory = inventoryService.findByProductId(productId);
        ProductDto dto = mapper.toDto(product, inventory);

        // 3. Populate cache
        cache.put(dto);
        return dto;
    }

    // ── Cache MISS path: load from DB ───────────────────────────────

    private List<ProductDto> loadFromDbAndCache(int page, int size,
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
        Map<String, Inventory> inventoryMap = inventoryService.findMapByProducts(products);
        List<ProductDto> dtos = mapper.toDtos(products, inventoryMap);

        cache.putPageIds(pageKey, ids);
        cache.putAll(dtos);
        return dtos;
    }

    // ── Cache HIT path: resolve from cache ──────────────────────────

    private List<ProductDto> resolveFromCache(List<String> productIds) {
        if (productIds.isEmpty()) return Collections.emptyList();

        List<ProductDto> cached = cache.getAll(productIds);
        Map<String, ProductDto> resultMap = new LinkedHashMap<>();
        List<String> missingIds = new ArrayList<>();

        for (int i = 0; i < productIds.size(); i++) {
            ProductDto dto = cached.get(i);
            if (dto != null) {
                resultMap.put(productIds.get(i), dto);
            } else {
                missingIds.add(productIds.get(i));
            }
        }

        if (!missingIds.isEmpty()) {
            fillMissingFromDb(missingIds, resultMap);
        }

        // Reassemble in original order
        return productIds.stream()
                .map(resultMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private void fillMissingFromDb(List<String> missingIds, Map<String, ProductDto> resultMap) {
        List<Product> products = queryService.findByIds(missingIds);
        Map<String, Inventory> inventoryMap = inventoryService.findMapByProducts(products);
        List<ProductDto> dtos = mapper.toDtos(products, inventoryMap);
        cache.putAll(dtos);
        dtos.forEach(dto -> resultMap.put(dto.getProductId(), dto));
    }

    // ── Utility ─────────────────────────────────────────────────────

    private static String defaultIfBlank(String value, String fallback) {
        return (value != null && !value.isBlank()) ? value : fallback;
    }
}
