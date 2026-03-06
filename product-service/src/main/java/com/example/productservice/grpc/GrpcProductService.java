package com.example.productservice.grpc;

import com.example.productservice.coreapi.queries.dto.ProductDto;
import com.example.productservice.query.service.ProductReadService;
import com.example.productservicegrpc.grpc.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;

/**
 * gRPC Server implementation cho Product Service.
 * Các service khác (cart-service, order-service, ...) gọi qua gRPC đến đây.
 */
@Slf4j
@GrpcService
@RequiredArgsConstructor
public class GrpcProductService extends ProductGrpcServiceGrpc.ProductGrpcServiceImplBase {

    private final ProductReadService productReadService;

    @Override
    public void getProductById(com.example.productservicegrpc.grpc.GetProductByIdRequest request, StreamObserver<com.example.productservicegrpc.grpc.ProductResponse> responseObserver) {
        super.getProductById(request, responseObserver);
        try {
            String productId = request.getProductId();
            log.debug("gRPC getProductById: {}", productId);

            ProductDto dto = productReadService.findProductById(productId);

            if (dto == null) {
                responseObserver.onNext(ProductResponse.newBuilder()
                        .setFound(false)
                        .build());
            } else {
                responseObserver.onNext(ProductResponse.newBuilder()
                        .setFound(true)
                        .setProduct(toProductMessage(dto))
                        .build());
            }
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("gRPC getProductById error: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getProductsByIds(com.example.productservicegrpc.grpc.GetProductsByIdsRequest request, StreamObserver<com.example.productservicegrpc.grpc.ProductListResponse> responseObserver) {
        super.getProductsByIds(request, responseObserver);
        try {
            List<String> productIds = request.getProductIdsList();
            log.debug("gRPC getProductsByIds: {}", productIds);

            List<ProductDto> dtos = productReadService.findProductsByIds(productIds);

            ProductListResponse.Builder builder = ProductListResponse.newBuilder();
            for (ProductDto dto : dtos) {
                builder.addProducts(toProductMessage(dto));
            }

            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("gRPC getProductsByIds error: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }


    // ─── Mapper: ProductDto → ProductMessage (protobuf) ───

    private ProductMessage toProductMessage(ProductDto dto) {
        return ProductMessage.newBuilder()
                .setProductId(dto.getProductId() != null ? dto.getProductId() : "")
                .setName(dto.getName() != null ? dto.getName() : "")
                .setPrice(dto.getPrice() != null ? dto.getPrice().toPlainString() : "0")
                .setImageUrl(dto.getImageUrl() != null ? dto.getImageUrl() : "")
                .build();
    }
}

