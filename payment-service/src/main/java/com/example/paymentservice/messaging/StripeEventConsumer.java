package com.example.paymentservice.messaging;

import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.ApiResource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.paymentservice.service.OrderService;
import com.example.paymentservice.service.IdempotencyService;

@Service
public class StripeEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(StripeEventConsumer.class);

    private final OrderService orderService;
    private final IdempotencyService idempotencyService;

    public StripeEventConsumer(OrderService orderService, IdempotencyService idempotencyService) {
        this.orderService = orderService;
        this.idempotencyService = idempotencyService;
    }

    @KafkaListener(topics = "${kafka.topic.stripe-events}", groupId = "payment-service-group")
    public void consumeStripeEvent(String rawJsonPayload) {
        // Parse event using Stripe SDK's Gson configuration
        Event event = ApiResource.GSON.fromJson(rawJsonPayload, Event.class);
        String eventId = event.getId();

        // Kiểm tra Idempotency (Chống trùng lặp)
        if (idempotencyService.hasProcessed(eventId)) {
            log.info("Event {} duplicated. Skipping.", eventId);
            return;
        }

        try {
            switch (event.getType()) {
                case "checkout.session.completed":
                    // Deserialize the inner data object
                    Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
                    if (session != null) {
                        log.info("Processing payment for Order: {}", session.getClientReferenceId());
                        orderService.updateOrderToPaid(session);
                    }
                    break;
                // Add more cases as needed
                default:
                    log.debug("Unhandled event type: {}", event.getType());
            }
            idempotencyService.markAsProcessed(eventId);
        } catch (Exception e) {
            log.error("Failed to process event {}", eventId, e);
            throw e; // Để Kafka retry
        }
    }
}

