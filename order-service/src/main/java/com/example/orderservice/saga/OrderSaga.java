package com.example.orderservice.saga;
/*
import com.example.orderservice.coreapi.events.OrderCreatedEvent;
// Import các Event/Command từ các Service khác (Giả định nằm trong thư mục coreapi chung)
import com.example.paymentservice.coreapi.events.PaymentCompletedEvent;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Saga
public class OrderSaga {

    private static final Logger log = LoggerFactory.getLogger(OrderSaga.class);

    @Autowired
    private transient CommandGateway commandGateway;

    // --- SAGA STATE ---
    // Biến này sẽ được Axon tự động serialize và lưu vào Database (bảng saga_entry)
    // Giúp Saga "nhớ" được user nào đang mua hàng để cuối cùng xóa đúng Cart của user đó
    private Long userId;

    // ==========================================
    // BƯỚC 1: Bắt đầu SAGA khi Order được tạo
    // ==========================================
    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCreatedEvent event) {
        log.info("Saga started cho Order: {}", event.getOrderId());
        
        // Lưu trạng thái userId vào Saga
        this.userId = event.getUserId(); 

        // Gửi Command sang Product Service để giữ hàng
        // Giả định bạn có lớp ReserveProductCommand
        // commandGateway.send(new ReserveProductCommand(event.getOrderId(), event.getItems()));
        
        // TẠM THỜI (Nếu bỏ qua bước giữ hàng): Đi thẳng đến tạo Payment
        String paymentId = UUID.randomUUID().toString();
        log.info("Đang tạo yêu cầu thanh toán với PaymentId: {}", paymentId);
        // commandGateway.send(new CreatePaymentCommand(paymentId, event.getOrderId(), event.getTotalAmount(), "STRIPE"));
    }

    // ==========================================
    // BƯỚC 2: Hàng hóa đã được giữ (Nếu có Product Service)
    // ==========================================
    // @SagaEventHandler(associationProperty = "orderId")
    // public void handle(ProductReservedEvent event) {
    //     // Gửi Command sang Payment Service
    // }

    // ==========================================
    // BƯỚC 3: Xử lý sau khi Webhook Stripe báo Thanh Toán Xong
    // ==========================================
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentCompletedEvent event) {
        log.info("Nhận xác nhận thanh toán thành công cho Order: {}. Đang yêu cầu xóa Cart.", event.getOrderId());

        // Khách thanh toán xong -> Xóa sạch giỏ hàng của khách (Gọi sang Cart Service)
        // Giả định bạn có lớp ClearCartCommand(String userId, String orderId)
        // commandGateway.send(new ClearCartCommand(String.valueOf(this.userId), event.getOrderId()));
    }

    // ==========================================
    // BƯỚC 4: Xóa Cart thành công -> Hoàn tất Đơn hàng
    // ==========================================
    // @SagaEventHandler(associationProperty = "orderId")
    // public void handle(CartClearedEvent event) {
    //     log.info("Đã xóa Cart cho User. Cập nhật trạng thái Order thành COMPLETED.");
    //     commandGateway.send(new ApproveOrderCommand(event.getOrderId()));
    // }

    // ==========================================
    // BƯỚC 5: Kết thúc SAGA thành công
    // ==========================================
    // @SagaEventHandler(associationProperty = "orderId")
    // @EndSaga
    // public void handle(OrderApprovedEvent event) {
    //     log.info("Quy trình Order hoàn tất mỹ mãn cho Order: {}", event.getOrderId());
    // }


    // ==========================================
    // LUỒNG BÙ TRỪ (COMPENSATION PATH - Nếu có lỗi)
    // ==========================================
    
    // Trường hợp 1: Hết hàng trong kho
    // @SagaEventHandler(associationProperty = "orderId")
    // @EndSaga
    // public void handle(ProductReservationFailedEvent event) {
    //     log.error("Kho hết hàng! Hủy đơn {}", event.getOrderId());
    //     commandGateway.send(new RejectOrderCommand(event.getOrderId(), "Sản phẩm không đủ số lượng tồn kho"));
    // }

    // Trường hợp 2: Thanh toán thất bại
    // @SagaEventHandler(associationProperty = "orderId")
    // @EndSaga
    // public void handle(PaymentFailedEvent event) {
    //     log.error("Thanh toán thất bại cho Order {}. Bắt đầu Rollback.", event.getOrderId());
    //     // 1. Nhả lại hàng hóa đã giữ trong kho
    //     // commandGateway.send(new CancelProductReservationCommand(event.getOrderId()));
    //     // 2. Hủy đơn hàng
    //     // commandGateway.send(new RejectOrderCommand(event.getOrderId(), "Thanh toán thất bại"));
    // }
}

 */