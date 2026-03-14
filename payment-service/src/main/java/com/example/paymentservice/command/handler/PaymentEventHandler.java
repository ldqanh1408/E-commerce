package com.example.paymentservice.command.handler;

import com.example.paymentservice.coreapi.events.PaymentCompletedEvent;
import com.example.paymentservice.coreapi.events.PaymentCreatedEvent;
import com.example.paymentservice.coreapi.events.PaymentFailedEvent;
import com.example.paymentservice.query.entity.Payment;
import com.example.paymentservice.query.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Event Handler (Query side):
 * Lắng nghe event từ Axon → cập nhật Read DB (PostgreSQL).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventHandler {

    private final PaymentService paymentService;

    @EventHandler
    public void on(PaymentCreatedEvent event) {
        log.info("Handling PaymentCreatedEvent: {}", event.getPaymentId());

        Payment payment = new Payment();
        payment.setPaymentId(event.getPaymentId());
        payment.setOrderId(event.getOrderId());
        payment.setAmount(event.getAmount());
        payment.setStatus("PENDING");
        payment.setPaymentMethod(event.getPaymentMethod());
        payment.setCreatedAt(Instant.now());

        paymentService.save(payment);

        log.info("Đã lưu thanh toán {} vào Read DB với trạng thái PENDING", event.getPaymentId());
    }

    @EventHandler
    public void on(PaymentCompletedEvent event) {
        log.info("Handling PaymentCompletedEvent: {}", event.getPaymentId());

        Payment payment = paymentService.findByPaymentId(event.getPaymentId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thanh toán: " + event.getPaymentId()));

        payment.setStatus("COMPLETED");
        payment.setTransactionId(event.getTransactionId());

        paymentService.save(payment);

        log.info("Đã cập nhật thanh toán {} → COMPLETED", event.getPaymentId());
    }

    @EventHandler
    public void on(PaymentFailedEvent event) {
        log.info("Handling PaymentFailedEvent: {}", event.getPaymentId());

        Payment payment = paymentService.findByPaymentId(event.getPaymentId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thanh toán: " + event.getPaymentId()));

        payment.setStatus("FAILED");

        paymentService.save(payment);

        log.info("Đã cập nhật thanh toán {} → FAILED, lý do: {}", event.getPaymentId(), event.getReason());
    }
}
