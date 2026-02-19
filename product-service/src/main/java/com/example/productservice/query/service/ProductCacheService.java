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

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductCacheService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final Duration PRODUCT_TTL = Duration.ofDays(1);
    private static final Duration PAGE_TTL = Duration.ofMinutes(5);
    private static final String PRODUCT_KEY_PREFIX = "product:";
    private static final String PAGE_KEY_PREFIX = "products:page:ids:";
    private static final String CURSOR_KEY_PREFIX = "products:cursor:ids:";

    // ── Single product cache ────────────────────────────────────────

    public void cacheProduct(ProductDto product) {
        String key = PRODUCT_KEY_PREFIX + product.getProductId();
        try {
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(product), PRODUCT_TTL);
        } catch (Exception e) {
            log.error("Error caching product {}: {}", product.getProductId(), e.getMessage());
        }
    }

    public ProductDto getProduct(String productId) {
        String key = PRODUCT_KEY_PREFIX + productId;
        try {
            String json = redisTemplate.opsForValue().get(key);
            if (json != null) {
                return objectMapper.readValue(json, ProductDto.class);
            }
        } catch (Exception e) {
            log.error("Error reading product {} from cache: {}", productId, e.getMessage());
        }
        return null;
    }

    public void evictProduct(String productId) {
        try {
            redisTemplate.delete(PRODUCT_KEY_PREFIX + productId);
        } catch (Exception e) {
            log.error("Error evicting product from cache: {}", e.getMessage());
        }
    }

    // ── Multi-product cache (pipeline) ──────────────────────────────

    public void cacheProducts(List<ProductDto> products) {
        Map<String, String> entries = new HashMap<>();
        for (ProductDto p : products) {
            try {
                entries.put(PRODUCT_KEY_PREFIX + p.getProductId(), objectMapper.writeValueAsString(p));
            } catch (Exception e) {
                log.error("Error serializing product {}: {}", p.getProductId(), e.getMessage());
            }
        }
        if (entries.isEmpty()) return;

        try {
            long ttlSeconds = PRODUCT_TTL.toSeconds();
            redisTemplate.executePipelined((org.springframework.data.redis.core.RedisCallback<Object>) connection -> {
                entries.forEach((key, value) -> {
                    byte[] k = redisTemplate.getStringSerializer().serialize(key);
                    byte[] v = redisTemplate.getStringSerializer().serialize(value);
                    connection.setEx(k, ttlSeconds, v);
                });
                return null;
            });
        } catch (Exception e) {
            log.error("Error caching products batch: {}", e.getMessage());
        }
    }

    public List<ProductDto> getProducts(List<String> productIds) {
        List<String> keys = productIds.stream().map(id -> PRODUCT_KEY_PREFIX + id).toList();
        try {
            List<String> jsonList = redisTemplate.opsForValue().multiGet(keys);
            if (jsonList == null) return Collections.emptyList();

            return jsonList.stream()
                    .map(json -> {
                        if (json == null) return null;
                        try {
                            return objectMapper.readValue(json, ProductDto.class);
                        } catch (Exception e) {
                            log.error("Error deserializing cached product: {}", e.getMessage());
                            return null;
                        }
                    })
                    .toList();
        } catch (Exception e) {
            log.error("Error multi-get products from cache: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    // ── Page ID cache ───────────────────────────────────────────────

    public void cacheProductPageIds(String key, List<String> productIds) {
        try {
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(productIds), PAGE_TTL);
        } catch (Exception e) {
            log.error("Error caching page IDs: {}", e.getMessage());
        }
    }

    public List<String> getProductPageIds(String key) {
        try {
            String json = redisTemplate.opsForValue().get(key);
            if (json != null) {
                return objectMapper.readValue(json, new TypeReference<>() {});
            }
        } catch (Exception e) {
            log.error("Error reading page IDs from cache: {}", e.getMessage());
        }
        return null;
    }

    // ── Key builders ────────────────────────────────────────────────

    public String buildPageKey(int page, int size, String sort, String order) {
        return PAGE_KEY_PREFIX + page + ":size:" + size + ":sort:" + sort + ":" + order;
    }

    public String buildCursorKey(String lastId, String lastValue, int size, String sort, String order) {
        return CURSOR_KEY_PREFIX + lastId + ":" + lastValue + ":size:" + size + ":sort:" + sort + ":" + order;
    }
}
