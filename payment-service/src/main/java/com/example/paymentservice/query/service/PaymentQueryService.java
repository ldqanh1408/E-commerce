package com.example.paymentservice.query.service;

import com.example.paymentservice.coreapi.queries.dto.PaymentDto;
import com.example.paymentservice.query.entity.Payment;
import com.example.paymentservice.query.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentQueryService {

    private final PaymentRepository paymentRepository;

    public PaymentDto findByPaymentId(String paymentId) {
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thanh toán: " + paymentId));
        return toDto(payment);
    }

    public PaymentDto findByOrderId(String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thanh toán cho đơn hàng: " + orderId));
        return toDto(payment);
    }

    private PaymentDto toDto(Payment payment) {
        return PaymentDto.builder()
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

