package com.example.paymentservice.command.aggregate;

import com.example.paymentapi.commands.CreatePaymentCommand;
import com.example.paymentapi.commands.CompletePaymentCommand;
import com.example.paymentapi.commands.FailPaymentCommand;
import com.example.paymentapi.events.PaymentCreatedEvent;
import com.example.paymentapi.events.PaymentCompletedEvent;
import com.example.paymentapi.events.PaymentFailedEvent;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.math.BigDecimal;

@Aggregate
@NoArgsConstructor
@Slf4j
public class PaymentAggregate {

    @AggregateIdentifier
    private String paymentId;
    private String orderId;
    private BigDecimal amount;
    private String paymentMethod;
    private String transactionId;
    private String status; // PENDING, COMPLETED, FAILED

    // ── Command Handlers ────────────────────────────────────────────

    @CommandHandler
    public PaymentAggregate(CreatePaymentCommand cmd) {
        log.info("Handling CreatePaymentCommand: {}", cmd.getPaymentId());

        AggregateLifecycle.apply(PaymentCreatedEvent.builder()
                .paymentId(cmd.getPaymentId())
                .orderId(cmd.getOrderId())
                .amount(cmd.getAmount())
                .paymentMethod(cmd.getPaymentMethod())
                .build());
    }

    @CommandHandler
    public void handle(CompletePaymentCommand cmd) {
        if ("COMPLETED".equals(this.status)) {
            throw new IllegalStateException("Thanh toán đã hoàn thành trước đó");
        }
        if ("FAILED".equals(this.status)) {
            throw new IllegalStateException("Thanh toán đã thất bại, không thể hoàn thành");
        }
        log.info("Handling CompletePaymentCommand: {}", cmd.getPaymentId());

        AggregateLifecycle.apply(PaymentCompletedEvent.builder()
                .paymentId(cmd.getPaymentId())
                .orderId(this.orderId)
                .amount(this.amount)
                .paymentMethod(this.paymentMethod)
                .transactionId(cmd.getTransactionId())
                .build());
    }

    @CommandHandler
    public void handle(FailPaymentCommand cmd) {
        if ("COMPLETED".equals(this.status)) {
            throw new IllegalStateException("Thanh toán đã hoàn thành, không thể đánh dấu thất bại");
        }
        if ("FAILED".equals(this.status)) {
            throw new IllegalStateException("Thanh toán đã thất bại trước đó");
        }
        log.info("Handling FailPaymentCommand: {}", cmd.getPaymentId());

        AggregateLifecycle.apply(PaymentFailedEvent.builder()
                .paymentId(cmd.getPaymentId())
                .orderId(this.orderId)
                .amount(this.amount)
                .paymentMethod(this.paymentMethod)
                .reason(cmd.getReason())
                .build());
    }

    // ── Event Sourcing Handlers (Rebuild state) ─────────────────────

    @EventSourcingHandler
    public void on(PaymentCreatedEvent event) {
        this.paymentId = event.getPaymentId();
        this.orderId = event.getOrderId();
        this.amount = event.getAmount();
        this.paymentMethod = event.getPaymentMethod();
        this.status = "PENDING";
    }

    @EventSourcingHandler
    public void on(PaymentCompletedEvent event) {
        this.status = "COMPLETED";
        this.transactionId = event.getTransactionId();
    }

    @EventSourcingHandler
    public void on(PaymentFailedEvent event) {
        this.status = "FAILED";
    }
}
