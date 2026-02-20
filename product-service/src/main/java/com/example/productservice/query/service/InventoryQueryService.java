package com.example.productservice.query.service;

import com.example.productservice.query.entity.Inventory;
import com.example.productservice.query.entity.Product;
import com.example.productservice.query.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * DB access ONLY — inventory queries.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryQueryService {

    private final InventoryRepository inventoryRepo;

    public Inventory findByProductId(String productId) {
        return inventoryRepo.findByProductId(productId).orElse(null);
    }

    public Map<String, Inventory> findMapByProducts(List<Product> products) {
        List<String> ids = products.stream().map(Product::getProductId).toList();
        return inventoryRepo.findByProductIdIn(ids).stream()
                .collect(Collectors.toMap(Inventory::getProductId, Function.identity()));
    }
}

