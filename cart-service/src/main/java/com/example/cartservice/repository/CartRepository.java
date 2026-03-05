package com.example.cartservice.repository;

import com.example.cartservice.model.CartItem;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class CartRepository {

    private static final String CART_KEY_PREFIX = "cart:";

    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, CartItem> hashOperations;

    @PostConstruct
    private void init() {
        hashOperations = redisTemplate.opsForHash();
    }

    /**
     * Lấy tất cả items trong giỏ hàng của user.
     * @return Map<productId, CartItem>
     */
    public Map<String, CartItem> findByUsername(String username) {
        return hashOperations.entries(getKey(username));
    }

    /**
     * Thêm hoặc cập nhật item trong giỏ hàng.
     */
    public void addItem(String username, CartItem item) {
        hashOperations.put(getKey(username), item.getProductId(), item);
    }

    /**
     * Xoá 1 item khỏi giỏ hàng.
     */
    public void removeItem(String username, String productId) {
        hashOperations.delete(getKey(username), productId);
    }

    /**
     * Xoá toàn bộ giỏ hàng.
     */
    public void clearCart(String username) {
        redisTemplate.delete(getKey(username));
    }

    private String getKey(String username) {
        return CART_KEY_PREFIX + username;
    }
}

