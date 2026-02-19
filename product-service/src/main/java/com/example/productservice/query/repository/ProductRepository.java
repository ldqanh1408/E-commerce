package com.example.productservice.query.repository;

import com.example.productservice.query.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProductId(String productId);

    List<Product> findByProductIdIn(List<String> productIds);

    // Keyset pagination by ID
    List<Product> findByProductIdGreaterThanOrderByProductIdAsc(String lastId, Pageable pageable);

    List<Product> findByProductIdLessThanOrderByProductIdDesc(String lastId, Pageable pageable);

    // Keyset pagination by price
    @Query("SELECT p FROM Product p WHERE p.price > :lastPrice OR (p.price = :lastPrice AND p.productId > :lastId) ORDER BY p.price ASC, p.productId ASC")
    List<Product> findByPriceAscCursor(@Param("lastPrice") BigDecimal lastPrice, @Param("lastId") String lastId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.price < :lastPrice OR (p.price = :lastPrice AND p.productId < :lastId) ORDER BY p.price DESC, p.productId DESC")
    List<Product> findByPriceDescCursor(@Param("lastPrice") BigDecimal lastPrice, @Param("lastId") String lastId, Pageable pageable);
}
