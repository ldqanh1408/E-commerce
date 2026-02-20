package com.example.productservice.query.service;

import com.example.productservice.query.entity.Product;
import com.example.productservice.query.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * DB access ONLY — product queries with pagination.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductQueryService {

    private final ProductRepository productRepo;

    public Product findById(String productId) {
        return productRepo.findByProductId(productId).orElse(null);
    }

    public List<Product> findByIds(List<String> productIds) {
        return productRepo.findByProductIdIn(productIds);
    }

    public List<Product> findByOffset(int page, int size, String sortBy, String sortOrder) {
        Sort.Direction dir = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return productRepo.findAll(PageRequest.of(page, size, Sort.by(dir, sortBy))).getContent();
    }

    public List<Product> findByKeyset(String lastId, String lastValue,
                                       int size, String sortBy, String sortOrder) {
        PageRequest pageable = PageRequest.of(0, size);
        boolean desc = "desc".equalsIgnoreCase(sortOrder);

        if ("price".equalsIgnoreCase(sortBy)) {
            BigDecimal price = parseBigDecimal(lastValue);
            return desc
                    ? productRepo.findByPriceDescCursor(price, lastId, pageable)
                    : productRepo.findByPriceAscCursor(price, lastId, pageable);
        }

        return desc
                ? productRepo.findByProductIdLessThanOrderByProductIdDesc(lastId, pageable)
                : productRepo.findByProductIdGreaterThanOrderByProductIdAsc(lastId, pageable);
    }

    private static BigDecimal parseBigDecimal(String value) {
        if (value == null) return BigDecimal.ZERO;
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}

