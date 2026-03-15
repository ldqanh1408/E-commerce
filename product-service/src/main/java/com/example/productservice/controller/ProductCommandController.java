package com.example.productservice.controller;

import com.example.productapi.commands.CreateProductCommand;
import com.example.productapi.commands.DeleteProductCommand;
import com.example.productapi.commands.UpdateProductCommand;
import com.example.productapi.commands.dto.CreateProductRequest;
import com.example.productapi.commands.dto.UpdateProductRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Command Controller — xử lý các endpoint GHI dữ liệu (CQRS Command side).
 * Gửi command qua Axon CommandGateway → Aggregate xử lý → phát Event → EventHandler cập nhật Read DB.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductCommandController {

    private final CommandGateway commandGateway;

    /**
     * POST /api/v1/products — Tạo sản phẩm mới
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<String> createProduct(@Valid @RequestBody CreateProductRequest request) {
        String productId = UUID.randomUUID().toString();

        CreateProductCommand command = CreateProductCommand.builder()
                .productId(productId)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .sku(request.getSku())
                .imageUrl(request.getImageUrl())
                .categoryId(request.getCategoryId())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        try {
            commandGateway.sendAndWait(command);
            log.info("Tạo sản phẩm thành công: {}", productId);
            return ResponseEntity.status(HttpStatus.CREATED).body(productId);
        } catch (Exception e) {
            log.error("Lỗi khi tạo sản phẩm: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * PUT /api/v1/products/{productId} — Cập nhật sản phẩm
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{productId}")
    public ResponseEntity<String> updateProduct(
            @PathVariable String productId,
            @Valid @RequestBody UpdateProductRequest request) {

        UpdateProductCommand command = UpdateProductCommand.builder()
                .productId(productId)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .sku(request.getSku())
                .imageUrl(request.getImageUrl())
                .categoryId(request.getCategoryId())
                .active(request.getActive())
                .build();

        try {
            commandGateway.sendAndWait(command);
            log.info("Cập nhật sản phẩm thành công: {}", productId);
            return ResponseEntity.ok(productId);
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật sản phẩm {}: {}", productId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * DELETE /api/v1/products/{productId} — Xoá sản phẩm
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable String productId) {
        DeleteProductCommand command = new DeleteProductCommand(productId);

        try {
            commandGateway.sendAndWait(command);
            log.info("Xoá sản phẩm thành công: {}", productId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Lỗi khi xoá sản phẩm {}: {}", productId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
