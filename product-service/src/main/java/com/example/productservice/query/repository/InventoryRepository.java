package com.example.productservice.query.repository;

import com.example.productservice.query.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductId(String productId);
}

