package com.example.paymentservice.query.service;

import com.example.paymentservice.query.entity.Payment;
import com.example.paymentservice.query.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public void save(Payment payment) {
        paymentRepository.save(payment);
    }

    public Payment findPaymentEntity(String paymentId) {
        return paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thanh toán: " + paymentId));
    }

    public com.example.paymentapi.queries.dto.PaymentDto findByPaymentId(String paymentId) {
        return toDto(findPaymentEntity(paymentId));
    }

    public com.example.paymentapi.queries.dto.PaymentDto findByOrderId(String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thanh toán cho đơn hàng: " + orderId));
        return toDto(payment);
    }

    private com.example.paymentapi.queries.dto.PaymentDto toDto(Payment payment) {
        return com.example.paymentapi.queries.dto.PaymentDto.builder()
                .paymentId(payment.getPaymentId())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .paymentMethod(payment.getPaymentMethod())
                .transactionId(payment.getTransactionId())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
