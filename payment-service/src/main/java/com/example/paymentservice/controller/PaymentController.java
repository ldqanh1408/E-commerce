package com.example.paymentservice.controller;

import com.example.paymentservice.coreapi.commands.CompletePaymentCommand;
import com.example.paymentservice.coreapi.commands.CreatePaymentCommand;
import com.example.paymentservice.coreapi.commands.FailPaymentCommand;
import com.example.paymentservice.coreapi.queries.GetPaymentByOrderIdQuery;
import com.example.paymentservice.coreapi.queries.GetPaymentQuery;
import com.example.paymentservice.coreapi.queries.dto.PaymentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    // ── Command Endpoints ───────────────────────────────────────────

    @PostMapping
    public CompletableFuture<String> createPayment(@RequestBody CreatePaymentRequest request) {
        String paymentId = UUID.randomUUID().toString();
        log.info("Tạo thanh toán mới: {} cho đơn hàng: {}", paymentId, request.orderId());

        return commandGateway.send(CreatePaymentCommand.builder()
                .paymentId(paymentId)
                .orderId(request.orderId())
                .amount(request.amount())
                .paymentMethod(request.paymentMethod())
                .build());
    }

    @PutMapping("/{paymentId}/complete")
    public CompletableFuture<Void> completePayment(
            @PathVariable String paymentId,
            @RequestBody CompletePaymentRequest request) {
        log.info("Hoàn thành thanh toán: {}", paymentId);

        return commandGateway.send(CompletePaymentCommand.builder()
                .paymentId(paymentId)
                .transactionId(request.transactionId())
                .build());
    }

    @PutMapping("/{paymentId}/fail")
    public CompletableFuture<Void> failPayment(
            @PathVariable String paymentId,
            @RequestBody FailPaymentRequest request) {
        log.info("Đánh dấu thanh toán thất bại: {}", paymentId);

        return commandGateway.send(FailPaymentCommand.builder()
                .paymentId(paymentId)
                .reason(request.reason())
                .build());
    }

    // ── Query Endpoints ─────────────────────────────────────────────

    @GetMapping("/{paymentId}")
    public CompletableFuture<PaymentDto> getPayment(@PathVariable String paymentId) {
        return queryGateway.query(new GetPaymentQuery(paymentId), PaymentDto.class);
    }

    @GetMapping("/order/{orderId}")
    public CompletableFuture<PaymentDto> getPaymentByOrderId(@PathVariable String orderId) {
        return queryGateway.query(new GetPaymentByOrderIdQuery(orderId), PaymentDto.class);
    }

    // ── Request DTOs ────────────────────────────────────────────────

    public record CreatePaymentRequest(String orderId, BigDecimal amount, String paymentMethod) {}
    public record CompletePaymentRequest(String transactionId) {}
    public record FailPaymentRequest(String reason) {}
}


