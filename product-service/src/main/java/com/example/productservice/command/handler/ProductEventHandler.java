package com.example.productservice.command.handler;

import com.example.productapi.events.ProductCreatedEvent;
import com.example.productapi.events.ProductDeletedEvent;
import com.example.productapi.events.ProductUpdatedEvent;
import com.example.productservice.query.entity.Category;
import com.example.productservice.query.entity.Inventory;
import com.example.productservice.query.entity.Product;
import com.example.productservice.query.repository.CategoryRepository;
import com.example.productservice.query.repository.InventoryRepository;
import com.example.productservice.query.repository.ProductRepository;
import com.example.productservice.query.service.ProductCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Event Handler (Query side):
 * Lắng nghe event từ Axon → cập nhật Read DB (PostgreSQL) + invalidate cache.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventHandler {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final CategoryRepository categoryRepository;
    private final ProductCacheService cacheService;

    @EventHandler
    public void on(ProductCreatedEvent event) {
        log.info("Handling ProductCreatedEvent: {}", event.getProductId());

        Product product = new Product();
        product.setProductId(event.getProductId());
        product.setName(event.getName());
        product.setDescription(event.getDescription());
        product.setPrice(event.getPrice());
        product.setQuantity(0); // Sản phẩm mới, chưa nhập kho → available = 0
        product.setSku(event.getSku());
        product.setImageUrl(event.getImageUrl());
        product.setActive(event.getActive());
        product.setCreatedAt(Instant.now());

        if (event.getCategoryId() != null) {
            Category category = categoryRepository.findById(event.getCategoryId()).orElse(null);
            product.setCategory(category);
        }

        productRepository.save(product);

        // Tạo inventory mặc định cho sản phẩm mới
        Inventory inventory = new Inventory();
        inventory.setProductId(event.getProductId());
        inventory.setQuantity(0);
        inventory.setReserved(0);
        inventory.setUpdatedAt(Instant.now());
        inventoryRepository.save(inventory);

        log.info("Đã lưu sản phẩm {} vào Read DB", event.getProductId());
    }

    @EventHandler
    public void on(ProductUpdatedEvent event) {
        log.info("Handling ProductUpdatedEvent: {}", event.getProductId());

        Product product = productRepository.findByProductId(event.getProductId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm: " + event.getProductId()));

        if (event.getName() != null) product.setName(event.getName());
        if (event.getDescription() != null) product.setDescription(event.getDescription());
        if (event.getPrice() != null) product.setPrice(event.getPrice());
        if (event.getSku() != null) product.setSku(event.getSku());
        if (event.getImageUrl() != null) product.setImageUrl(event.getImageUrl());
        if (event.getActive() != null) product.setActive(event.getActive());

        if (event.getCategoryId() != null) {
            Category category = categoryRepository.findById(event.getCategoryId()).orElse(null);
            product.setCategory(category);
        }

        productRepository.save(product);

        // Invalidate cache
        cacheService.evict(event.getProductId());

        log.info("Đã cập nhật sản phẩm {} trong Read DB", event.getProductId());
    }

    @EventHandler
    public void on(ProductDeletedEvent event) {
        log.info("Handling ProductDeletedEvent: {}", event.getProductId());

        productRepository.findByProductId(event.getProductId())
                .ifPresent(productRepository::delete);

        inventoryRepository.findByProductId(event.getProductId())
                .ifPresent(inventoryRepository::delete);

        // Invalidate cache
        cacheService.evict(event.getProductId());

        log.info("Đã xoá sản phẩm {} khỏi Read DB", event.getProductId());
    }
}

