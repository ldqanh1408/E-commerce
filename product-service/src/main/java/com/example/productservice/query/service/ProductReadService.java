package com.example.productservice.query.service;

import com.example.productservice.coreapi.queries.dto.ProductDto;
import com.example.productservice.query.entity.Inventory;
import com.example.productservice.query.entity.Product;
import com.example.productservice.query.repository.InventoryRepository;
import com.example.productservice.query.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductReadService {

    private final ProductRepository productRepo;
    private final InventoryRepository inventoryRepo;
    private final ProductCacheService cacheService;

    /**
     * Main entry point: returns product list with cache-aside pattern.
     */
    public List<ProductDto> getProducts(int page, int size, String sortBy, String sortOrder,
                                        String lastId, String lastValue) {
        sortBy = sortBy != null ? sortBy : "id";
        sortOrder = sortOrder != null ? sortOrder : "asc";

        // 1. Build cache key
        String pageKey = (lastId != null && !lastId.isBlank())
                ? cacheService.buildCursorKey(lastId, lastValue, size, sortBy, sortOrder)
                : cacheService.buildPageKey(page, size, sortBy, sortOrder);

        // 2. Try page cache (list of IDs)
        List<String> productIds = cacheService.getProductPageIds(pageKey);

        if (productIds == null) {
            return fetchFromDbAndCache(page, size, sortBy, sortOrder, lastId, lastValue, pageKey);
        }
        return assembleFromCache(productIds);
    }

    public ProductDto getProductDetails(String productId) {
        Product product = productRepo.findByProductId(productId).orElse(null);
        if (product == null) return null;

        Inventory inventory = inventoryRepo.findByProductId(productId).orElse(null);
        return mapToDto(product, inventory);
    }

    public List<Product> findProductsByIds(List<String> productIds) {
        return productRepo.findByProductIdIn(productIds);
    }

    public List<ProductDto> mapProductsToDtos(List<Product> products) {
        if (products.isEmpty()) return Collections.emptyList();

        List<String> ids = products.stream().map(Product::getProductId).toList();
        Map<String, Inventory> inventoryMap = inventoryRepo.findByProductIdIn(ids).stream()
                .collect(Collectors.toMap(Inventory::getProductId, Function.identity()));

        return products.stream()
                .map(p -> mapToDto(p, inventoryMap.get(p.getProductId())))
                .collect(Collectors.toList());
    }

    // ── Private helpers ──────────────────────────────────────────────

    private List<ProductDto> fetchFromDbAndCache(int page, int size, String sortBy, String sortOrder,
                                                  String lastId, String lastValue, String pageKey) {
        List<Product> products = fetchProductsFromDb(page, size, sortBy, sortOrder, lastId, lastValue);

        if (products.isEmpty()) {
            cacheService.cacheProductPageIds(pageKey, Collections.emptyList());
            return Collections.emptyList();
        }

        List<String> ids = products.stream().map(Product::getProductId).toList();
        cacheService.cacheProductPageIds(pageKey, ids);

        List<ProductDto> dtos = mapProductsToDtos(products);
        cacheService.cacheProducts(dtos);
        return dtos;
    }

    private List<ProductDto> assembleFromCache(List<String> productIds) {
        List<ProductDto> cached = cacheService.getProducts(productIds);

        List<String> missingIds = new ArrayList<>();
        Map<String, ProductDto> map = new java.util.LinkedHashMap<>();

        for (int i = 0; i < productIds.size(); i++) {
            ProductDto dto = cached.get(i);
            if (dto != null) {
                map.put(productIds.get(i), dto);
            } else {
                missingIds.add(productIds.get(i));
            }
        }

        if (!missingIds.isEmpty()) {
            List<ProductDto> fetched = mapProductsToDtos(findProductsByIds(missingIds));
            cacheService.cacheProducts(fetched);
            fetched.forEach(dto -> map.put(dto.getProductId(), dto));
        }

        // Preserve original order
        return productIds.stream()
                .map(map::get)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<Product> fetchProductsFromDb(int page, int size, String sortBy, String sortOrder,
                                               String lastId, String lastValue) {
        if (lastId != null && !lastId.isBlank()) {
            return fetchByKeyset(size, sortBy, sortOrder, lastId, lastValue);
        }
        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return productRepo.findAll(PageRequest.of(page, size, Sort.by(direction, sortBy))).getContent();
    }

    private List<Product> fetchByKeyset(int size, String sortBy, String sortOrder,
                                         String lastId, String lastValue) {
        PageRequest pageable = PageRequest.of(0, size);

        if ("price".equalsIgnoreCase(sortBy)) {
            BigDecimal price;
            try {
                price = lastValue != null ? new BigDecimal(lastValue) : BigDecimal.ZERO;
            } catch (NumberFormatException e) {
                price = BigDecimal.ZERO;
            }
            return "desc".equalsIgnoreCase(sortOrder)
                    ? productRepo.findByPriceDescCursor(price, lastId, pageable)
                    : productRepo.findByPriceAscCursor(price, lastId, pageable);
        }

        return "desc".equalsIgnoreCase(sortOrder)
                ? productRepo.findByProductIdLessThanOrderByProductIdDesc(lastId, pageable)
                : productRepo.findByProductIdGreaterThanOrderByProductIdAsc(lastId, pageable);
    }

    private ProductDto mapToDto(Product p, Inventory inv) {
        return new ProductDto(
                p.getProductId(),
                p.getName(),
                p.getPrice(),
                inv != null ? inv.getQuantity() : 0,
                p.getImageUrl(),
                p.getDescription(),
                p.getSku(),
                p.getCategory() != null ? p.getCategory().getName() : null
        );
    }
}
