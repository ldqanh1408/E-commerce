package com.example.paymentservice.query.service;

import com.example.paymentservice.query.entity.Payment;
import com.example.paymentservice.query.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public void save(Payment payment) {
        paymentRepository.save(payment);
    }

    public Optional<Payment> findByPaymentId(String paymentId) {
        return paymentRepository.findByPaymentId(paymentId);
    }
}
