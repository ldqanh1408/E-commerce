package com.example.productservicegrpc.grpc;

import io.grpc.*;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.*;

import static io.grpc.MethodDescriptor.generateFullMethodName;

@jakarta.annotation.Generated(
    value = "by gRPC proto compiler",
    comments = "Source: product_service.proto")
public final class ProductGrpcServiceGrpc {

    private ProductGrpcServiceGrpc() {}

    public static final String SERVICE_NAME = "product.ProductGrpcService";

    // ─── Method Descriptors ───

    private static volatile MethodDescriptor<GetProductByIdRequest, ProductResponse> getGetProductByIdMethod;

    public static MethodDescriptor<GetProductByIdRequest, ProductResponse> getGetProductByIdMethod() {
        MethodDescriptor<GetProductByIdRequest, ProductResponse> method = getGetProductByIdMethod;
        if (method == null) {
            synchronized (ProductGrpcServiceGrpc.class) {
                method = getGetProductByIdMethod;
                if (method == null) {
                    getGetProductByIdMethod = method = MethodDescriptor.<GetProductByIdRequest, ProductResponse>newBuilder()
                            .setType(MethodDescriptor.MethodType.UNARY)
                            .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetProductById"))
                            .setSampledToLocalTracing(true)
                            .setRequestMarshaller(ProtoUtils.marshaller(GetProductByIdRequest.getDefaultInstance()))
                            .setResponseMarshaller(ProtoUtils.marshaller(ProductResponse.getDefaultInstance()))
                            .build();
                }
            }
        }
        return method;
    }

    private static volatile MethodDescriptor<GetProductsByIdsRequest, ProductListResponse> getGetProductsByIdsMethod;

    public static MethodDescriptor<GetProductsByIdsRequest, ProductListResponse> getGetProductsByIdsMethod() {
        MethodDescriptor<GetProductsByIdsRequest, ProductListResponse> method = getGetProductsByIdsMethod;
        if (method == null) {
            synchronized (ProductGrpcServiceGrpc.class) {
                method = getGetProductsByIdsMethod;
                if (method == null) {
                    getGetProductsByIdsMethod = method = MethodDescriptor.<GetProductsByIdsRequest, ProductListResponse>newBuilder()
                            .setType(MethodDescriptor.MethodType.UNARY)
                            .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetProductsByIds"))
                            .setSampledToLocalTracing(true)
                            .setRequestMarshaller(ProtoUtils.marshaller(GetProductsByIdsRequest.getDefaultInstance()))
                            .setResponseMarshaller(ProtoUtils.marshaller(ProductListResponse.getDefaultInstance()))
                            .build();
                }
            }
        }
        return method;
    }

    // ─── Stubs ───

    public static ProductGrpcServiceBlockingStub newBlockingStub(Channel channel) {
        return new ProductGrpcServiceBlockingStub(channel, CallOptions.DEFAULT);
    }

    public static ProductGrpcServiceStub newStub(Channel channel) {
        return new ProductGrpcServiceStub(channel, CallOptions.DEFAULT);
    }

    public static ProductGrpcServiceFutureStub newFutureStub(Channel channel) {
        return new ProductGrpcServiceFutureStub(channel, CallOptions.DEFAULT);
    }

    // ─── Base Implementation (Server-side) ───

    public static abstract class ProductGrpcServiceImplBase implements io.grpc.BindableService {

        public void getProductById(GetProductByIdRequest request, StreamObserver<ProductResponse> responseObserver) {
            ServerCalls.asyncUnimplementedUnaryCall(getGetProductByIdMethod(), responseObserver);
        }

        public void getProductsByIds(GetProductsByIdsRequest request, StreamObserver<ProductListResponse> responseObserver) {
            ServerCalls.asyncUnimplementedUnaryCall(getGetProductsByIdsMethod(), responseObserver);
        }

        @Override
        public final ServerServiceDefinition bindService() {
            return ServerServiceDefinition.builder(getServiceDescriptor())
                    .addMethod(getGetProductByIdMethod(),
                            ServerCalls.asyncUnaryCall(
                                    (request, responseObserver) -> getProductById(request, responseObserver)))
                    .addMethod(getGetProductsByIdsMethod(),
                            ServerCalls.asyncUnaryCall(
                                    (request, responseObserver) -> getProductsByIds(request, responseObserver)))
                    .build();
        }
    }

    // ─── Blocking Stub (Synchronous Client) ───

    public static final class ProductGrpcServiceBlockingStub
            extends AbstractBlockingStub<ProductGrpcServiceBlockingStub> {

        private ProductGrpcServiceBlockingStub(Channel channel, CallOptions callOptions) {
            super(channel, callOptions);
        }

        @Override
        protected ProductGrpcServiceBlockingStub build(Channel channel, CallOptions callOptions) {
            return new ProductGrpcServiceBlockingStub(channel, callOptions);
        }

        public ProductResponse getProductById(GetProductByIdRequest request) {
            return ClientCalls.blockingUnaryCall(getChannel(), getGetProductByIdMethod(), getCallOptions(), request);
        }

        public ProductListResponse getProductsByIds(GetProductsByIdsRequest request) {
            return ClientCalls.blockingUnaryCall(getChannel(), getGetProductsByIdsMethod(), getCallOptions(), request);
        }
    }

    // ─── Async Stub ───

    public static final class ProductGrpcServiceStub
            extends AbstractAsyncStub<ProductGrpcServiceStub> {

        private ProductGrpcServiceStub(Channel channel, CallOptions callOptions) {
            super(channel, callOptions);
        }

        @Override
        protected ProductGrpcServiceStub build(Channel channel, CallOptions callOptions) {
            return new ProductGrpcServiceStub(channel, callOptions);
        }

        public void getProductById(GetProductByIdRequest request, StreamObserver<ProductResponse> responseObserver) {
            ClientCalls.asyncUnaryCall(getChannel().newCall(getGetProductByIdMethod(), getCallOptions()), request, responseObserver);
        }

        public void getProductsByIds(GetProductsByIdsRequest request, StreamObserver<ProductListResponse> responseObserver) {
            ClientCalls.asyncUnaryCall(getChannel().newCall(getGetProductsByIdsMethod(), getCallOptions()), request, responseObserver);
        }
    }

    // ─── Future Stub ───

    public static final class ProductGrpcServiceFutureStub
            extends AbstractFutureStub<ProductGrpcServiceFutureStub> {

        private ProductGrpcServiceFutureStub(Channel channel, CallOptions callOptions) {
            super(channel, callOptions);
        }

        @Override
        protected ProductGrpcServiceFutureStub build(Channel channel, CallOptions callOptions) {
            return new ProductGrpcServiceFutureStub(channel, callOptions);
        }

        public com.google.common.util.concurrent.ListenableFuture<ProductResponse> getProductById(GetProductByIdRequest request) {
            return ClientCalls.futureUnaryCall(getChannel().newCall(getGetProductByIdMethod(), getCallOptions()), request);
        }

        public com.google.common.util.concurrent.ListenableFuture<ProductListResponse> getProductsByIds(GetProductsByIdsRequest request) {
            return ClientCalls.futureUnaryCall(getChannel().newCall(getGetProductsByIdsMethod(), getCallOptions()), request);
        }
    }

    // ─── Service Descriptor ───

    private static volatile ServiceDescriptor serviceDescriptor;

    public static ServiceDescriptor getServiceDescriptor() {
        ServiceDescriptor result = serviceDescriptor;
        if (result == null) {
            synchronized (ProductGrpcServiceGrpc.class) {
                result = serviceDescriptor;
                if (result == null) {
                    serviceDescriptor = result = ServiceDescriptor.newBuilder(SERVICE_NAME)
                            .addMethod(getGetProductByIdMethod())
                            .addMethod(getGetProductsByIdsMethod())
                            .build();
                }
            }
        }
        return result;
    }
}
