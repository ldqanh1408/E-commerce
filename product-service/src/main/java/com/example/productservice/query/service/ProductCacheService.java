package com.example.productservice.query.service;

import com.example.productservice.coreapi.queries.dto.ProductDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Redis cache layer for product data.
 * <p>
 * Naming convention:
 *   put / get / evict  — single item
 *   putAll / getAll    — batch items
 *   putPageIds / getPageIds — page-level ID list
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductCacheService {

    private final StringRedisTemplate redis;
    private final ObjectMapper json;

    private static final Duration PRODUCT_TTL = Duration.ofDays(1);
    private static final Duration PAGE_TTL = Duration.ofMinutes(5);

    private static final String PRODUCT_PREFIX = "product:";
    private static final String PAGE_PREFIX = "products:page:";
    private static final String CURSOR_PREFIX = "products:cursor:";

    // ── Single product ──────────────────────────────────────────────

    public void put(ProductDto product) {
        writeJson(PRODUCT_PREFIX + product.getProductId(), product, PRODUCT_TTL);
    }

    public ProductDto get(String productId) {
        return readJson(PRODUCT_PREFIX + productId, ProductDto.class);
    }

    public void evict(String productId) {
        try {
            redis.delete(PRODUCT_PREFIX + productId);
        } catch (Exception e) {
            log.error("Failed to evict product {}: {}", productId, e.getMessage());
        }
    }

    // ── Batch products ──────────────────────────────────────────────

    public void putAll(List<ProductDto> products) {
        Map<String, String> entries = new HashMap<>();
        for (ProductDto p : products) {
            try {
                entries.put(PRODUCT_PREFIX + p.getProductId(), json.writeValueAsString(p));
            } catch (Exception e) {
                log.error("Failed to serialize product {}: {}", p.getProductId(), e.getMessage());
            }
        }
        if (entries.isEmpty()) return;

        try {
            long ttl = PRODUCT_TTL.toSeconds();
            redis.executePipelined((org.springframework.data.redis.core.RedisCallback<Object>) conn -> {
                entries.forEach((key, value) -> conn.stringCommands().setEx(
                        redis.getStringSerializer().serialize(key), ttl,
                        redis.getStringSerializer().serialize(value)
                ));
                return null;
            });
        } catch (Exception e) {
            log.error("Failed to cache product batch: {}", e.getMessage());
        }
    }

    public List<ProductDto> getAll(List<String> productIds) {
        List<String> keys = productIds.stream().map(id -> PRODUCT_PREFIX + id).toList();
        try {
            List<String> values = redis.opsForValue().multiGet(keys);
            if (values == null) return Collections.emptyList();

            return values.stream()
                    .map(v -> v != null ? readJsonUnsafe(v, ProductDto.class) : null)
                    .toList();
        } catch (Exception e) {
            log.error("Failed to multi-get products: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    // ── Page ID list ────────────────────────────────────────────────

    public void putPageIds(String pageKey, List<String> productIds) {
        writeJson(pageKey, productIds, PAGE_TTL);
    }

    public List<String> getPageIds(String pageKey) {
        try {
            String value = redis.opsForValue().get(pageKey);
            if (value != null) {
                return json.readValue(value, new TypeReference<>() {});
            }
        } catch (Exception e) {
            log.error("Failed to read page IDs [{}]: {}", pageKey, e.getMessage());
        }
        return null;
    }

    // ── Key builders ────────────────────────────────────────────────

    public String pageKey(int page, int size, String sort, String order) {
        return PAGE_PREFIX + page + ":" + size + ":" + sort + ":" + order;
    }

    public String cursorKey(String lastId, String lastValue, int size, String sort, String order) {
        return CURSOR_PREFIX + lastId + ":" + lastValue + ":" + size + ":" + sort + ":" + order;
    }

    // ── Internal helpers ────────────────────────────────────────────

    private void writeJson(String key, Object value, Duration ttl) {
        try {
            redis.opsForValue().set(key, json.writeValueAsString(value), ttl);
        } catch (Exception e) {
            log.error("Failed to write cache [{}]: {}", key, e.getMessage());
        }
    }

    private <T> T readJson(String key, Class<T> type) {
        try {
            String value = redis.opsForValue().get(key);
            if (value != null) {
                return json.readValue(value, type);
            }
        } catch (Exception e) {
            log.error("Failed to read cache [{}]: {}", key, e.getMessage());
        }
        return null;
    }

    private <T> T readJsonUnsafe(String value, Class<T> type) {
        try {
            return json.readValue(value, type);
        } catch (Exception e) {
            log.error("Failed to deserialize cache value: {}", e.getMessage());
            return null;
        }
    }
}
