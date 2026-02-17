package com.example.productservice.query.projection;

import com.example.productservice.coreapi.queries.GetProductDetailQuery;
import com.example.productservice.coreapi.queries.dto.ProductDto;
import com.example.productservice.query.entity.Product;
import com.example.productservice.query.entity.Inventory;
import com.example.productservice.query.repository.InventoryRepository;
import com.example.productservice.query.repository.ProductRepository;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductProjection {
    private final ProductRepository productRepo;
    private final InventoryRepository inventoryRepo;

    public ProductProjection(ProductRepository productRepo, InventoryRepository inventoryRepo) {
        this.productRepo = productRepo;
        this.inventoryRepo = inventoryRepo;
    }

    @QueryHandler
    public ProductDto handle(GetProductDetailQuery query) {
        Product entity = productRepo.findByProductId(query.getProductId()).orElse(null);
        if (entity == null) return null;

        Inventory inventory = inventoryRepo.findByProductId(query.getProductId()).orElse(null);
        Integer quantity = (inventory != null) ? inventory.getQuantity() : 0;

        // Map Entity -> DTO
        return new ProductDto(
                entity.getProductId(),
                entity.getName(),
                entity.getPrice(),
                quantity
        );
    }
}