package com.ecommerce.webhookrelay.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/webhooks")
public class StripeRelayController {

    private static final Logger log = LoggerFactory.getLogger(StripeRelayController.class);

    @Value("${kafka.topic.stripe-events}")
    private String stripeTopic;

    // STRIPE_WEBHOOK_SECRET được truyền TỰ ĐỘNG từ biến môi trường
    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    private final KafkaTemplate<String, String> kafkaTemplate;

    public StripeRelayController(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        try {
            // 1. CHỈ Verify chữ ký, KHÔNG deserialize thành object nặng nề
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

            // 2. Gửi NGAY LẬP TỨC chuỗi JSON thô vào Kafka
            kafkaTemplate.send(stripeTopic, event.getId(), payload);

            log.info("Relayed event {} to Kafka topic", event.getId());
            return ResponseEntity.ok().build();

        } catch (SignatureVerificationException e) {
            log.warn("Invalid Stripe signature detected!");
            return ResponseEntity.badRequest().body("Invalid signature");
        } catch (Exception e) {
            log.error("Error processing webhook payload", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}

