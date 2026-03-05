package com.example.cartservice.service;

import com.example.cartservice.client.ProductServiceClient;
import com.example.cartservice.dto.ProductDto;
import com.example.cartservice.dto.request.CartItemRequest;
import com.example.cartservice.dto.response.CartItemResponse;
import com.example.cartservice.dto.response.CartResponse;
import com.example.cartservice.model.CartItem;
import com.example.cartservice.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final ProductServiceClient productServiceClient;

    /**
     * Lấy giỏ hàng đầy đủ thông tin của user.
     */
    public CartResponse getCart(String username) {
        Map<String, CartItem> cartItems = cartRepository.findByUsername(username);

        if (cartItems == null || cartItems.isEmpty()) {
            return CartResponse.builder()
                    .userId(username)
                    .items(new ArrayList<>())
                    .totalPrice(BigDecimal.ZERO)
                    .build();
        }

        List<CartItemResponse> itemResponses = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems.values()) {
            ProductDto product = productServiceClient.getProductById(cartItem.getProductId());

            if (product == null) {
                log.warn("Sản phẩm {} không tìm thấy, bỏ qua khỏi giỏ hàng", cartItem.getProductId());
                continue;
            }

            CartItemResponse itemResponse = CartItemResponse.builder()
                    .productId(product.getProductId())
                    .name(product.getName())
                    .price(product.getPrice())
                    .quantity(cartItem.getQuantity())
                    .imageUrl(product.getImageUrl())
                    .build();

            itemResponses.add(itemResponse);

            BigDecimal itemTotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalPrice = totalPrice.add(itemTotal);
        }

        return CartResponse.builder()
                .userId(username)
                .items(itemResponses)
                .totalPrice(totalPrice)
                .build();
    }

    /**
     * Thêm mới hoặc cập nhật số lượng 1 item trong giỏ hàng.
     * Nếu productId đã tồn tại → ghi đè quantity.
     * Nếu chưa có → thêm mới.
     */
    public CartResponse addOrUpdateItem(String username, CartItemRequest request) {
        CartItem item = new CartItem(request.getProductId(), request.getQuantity());
        cartRepository.addItem(username, item);
        return getCart(username);
    }

    /**
     * Xoá 1 item khỏi giỏ hàng.
     */
    public CartResponse removeItem(String username, String productId) {
        cartRepository.removeItem(username, productId);
        return getCart(username);
    }

    /**
     * Xoá toàn bộ giỏ hàng.
     */
    public void clearCart(String username) {
        cartRepository.clearCart(username);
    }
}

