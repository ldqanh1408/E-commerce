package com.example.cartservice.client;

import com.example.cartservice.dto.ProductDto;
import com.example.productservicegrpc.grpc.*;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gọi product-service qua gRPC để lấy thông tin sản phẩm.
 */
@Service
@Slf4j
public class ProductServiceClient {

    @GrpcClient("product-service")
    private ProductGrpcServiceGrpc.ProductGrpcServiceBlockingStub productStub;

    /**
     * Lấy thông tin 1 sản phẩm theo ID qua gRPC.
     * Trả về null nếu sản phẩm không tồn tại hoặc service không khả dụng.
     */
    public ProductDto getProductById(String productId) {
        try {
            GetProductByIdRequest request = GetProductByIdRequest.newBuilder()
                    .setProductId(productId)
                    .build();

            ProductResponse response = productStub.getProductById(request);

            if (!response.getFound()) {
                log.warn("Sản phẩm {} không tìm thấy qua gRPC", productId);
                return null;
            }

            return toProductDto(response.getProduct());

        } catch (StatusRuntimeException e) {
            log.error("gRPC error khi lấy sản phẩm {}: {} - {}", productId, e.getStatus(), e.getMessage());
            return null;
        }
    }

    /**
     * Lấy thông tin nhiều sản phẩm theo danh sách ID qua gRPC.
     */
    public List<ProductDto> getProductsByIds(List<String> productIds) {
        try {
            GetProductsByIdsRequest request = GetProductsByIdsRequest.newBuilder()
                    .addAllProductIds(productIds)
                    .build();

            ProductListResponse response = productStub.getProductsByIds(request);

            return response.getProductsList().stream()
                    .map(this::toProductDto)
                    .collect(Collectors.toList());

        } catch (StatusRuntimeException e) {
            log.error("gRPC error khi lấy danh sách sản phẩm: {} - {}", e.getStatus(), e.getMessage());
            return Collections.emptyList();
        }
    }

    // ─── Mapper: ProductMessage (protobuf) → ProductDto ───

    private ProductDto toProductDto(ProductMessage msg) {
        return new ProductDto(
                msg.getProductId(),
                msg.getName(),
                new BigDecimal(msg.getPrice()),
                msg.getImageUrl()
        );
    }
}
