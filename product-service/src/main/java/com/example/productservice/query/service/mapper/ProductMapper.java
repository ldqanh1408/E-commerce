package com.example.productservice.query.service.mapper;

import com.example.productservice.coreapi.queries.dto.ProductDto;
import com.example.productservice.query.entity.Inventory;
import com.example.productservice.query.entity.Product;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Pure mapper: Product + Inventory → ProductDto.
 * Does NOT call any repository — inventory data must be provided by the caller.
 */
@Component
public class ProductMapper {

    public ProductDto toDto(Product product, Inventory inventory) {
        return new ProductDto(
                product.getProductId(),
                product.getName(),
                product.getPrice(),
                inventory != null ? inventory.getQuantity() : 0,
                product.getImageUrl(),
                product.getDescription(),
                product.getSku(),
                product.getCategory() != null ? product.getCategory().getName() : null
        );
    }

    public List<ProductDto> toDtos(List<Product> products, Map<String, Inventory> inventoryMap) {
        if (products.isEmpty()) return Collections.emptyList();

        return products.stream()
                .map(p -> toDto(p, inventoryMap.get(p.getProductId())))
                .collect(Collectors.toList());
    }
}
