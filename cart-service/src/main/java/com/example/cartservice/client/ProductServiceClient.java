package com.example.cartservice.client;

import com.example.cartservice.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceClient {

    private static final String PRODUCT_SERVICE_URL = "http://product-service/api/v1/products/";

    private final RestTemplate restTemplate;

    /**
     * Gọi product-service qua Eureka để lấy thông tin sản phẩm theo ID.
     * Trả về null nếu sản phẩm không tồn tại hoặc service không khả dụng.
     */
    public ProductDto getProductById(String productId) {
        try {
            return restTemplate.getForObject(
                    PRODUCT_SERVICE_URL + productId,
                    ProductDto.class
            );
        } catch (RestClientException e) {
            log.error("Không thể lấy thông tin sản phẩm {}: {}", productId, e.getMessage());
            return null;
        }
    }
}

