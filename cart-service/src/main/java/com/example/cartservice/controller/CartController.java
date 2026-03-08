package com.example.cartservice.controller;

import com.example.cartservice.dto.request.CartItemRequest;
import com.example.cartservice.dto.response.CartResponse;
import com.example.cartservice.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * GET /api/v1/cart — Xem giỏ hàng
     */
    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        CartResponse cart = cartService.getCart(username);
        return ResponseEntity.ok(cart);
    }

    /**
     * PUT /api/v1/cart/items — Thêm hoặc cập nhật số lượng 1 item
     * Nếu item đã có → cập nhật quantity
     * Nếu item chưa có → thêm mới
     */
    @PutMapping("/items")
    public ResponseEntity<CartResponse> addOrUpdateItem(@Valid @RequestBody CartItemRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        CartResponse cart = cartService.addOrUpdateItem(username, request);
        return ResponseEntity.ok(cart);
    }

    /**
     * DELETE /api/v1/cart/items/{productId} — Xoá 1 item khỏi giỏ
     */
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartResponse> removeItem(@PathVariable String productId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        CartResponse cart = cartService.removeItem(username, productId);
        return ResponseEntity.ok(cart);
    }

    /**
     * DELETE /api/v1/cart — Xoá toàn bộ giỏ hàng
     */
    @DeleteMapping
    public ResponseEntity<Void> clearCart() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        cartService.clearCart(username);
        return ResponseEntity.noContent().build();
    }
}
