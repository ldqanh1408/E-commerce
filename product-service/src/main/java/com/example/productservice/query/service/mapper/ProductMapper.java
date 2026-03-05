package com.example.productservice.query.service.mapper;

import com.example.productservice.coreapi.queries.dto.ProductDto;
import com.example.productservice.query.entity.Product;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Pure mapper: Product → ProductDto.
 * Không cần Inventory vì bảng products đã có cột quantity (Available = inventory.quantity - inventory.reserved).
 * Cột quantity được đồng bộ bởi EventHandler khi inventory thay đổi.
 */
@Component
public class ProductMapper {

    public ProductDto toDto(Product product) {
        return new ProductDto(
                product.getProductId(),
                product.getName(),
                product.getPrice(),
                product.getQuantity() != null ? product.getQuantity() : 0,
                product.getImageUrl(),
                product.getDescription(),
                product.getSku(),
                product.getCategory() != null ? product.getCategory().getName() : null
        );
    }

    public List<ProductDto> toDtos(List<Product> products) {
        if (products.isEmpty()) return Collections.emptyList();

        return products.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
