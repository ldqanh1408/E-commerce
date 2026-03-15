package com.example.paymentservice.messaging;

import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.ApiResource;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.paymentservice.service.OrderService;
import com.example.paymentservice.service.IdempotencyService;
import com.example.paymentservice.coreapi.commands.CompletePaymentCommand;

@Service
public class StripeEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(StripeEventConsumer.class);

    private final OrderService orderService;
    private final IdempotencyService idempotencyService;
    private final CommandGateway commandGateway;

    public StripeEventConsumer(OrderService orderService, IdempotencyService idempotencyService, CommandGateway commandGateway) {
        this.orderService = orderService;
        this.idempotencyService = idempotencyService;
        this.commandGateway = commandGateway;
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
                    if (session != null && session.getClientReferenceId() != null) {
                        log.info("Payment success for Order: {}. Sending CompletePaymentCommand.", session.getClientReferenceId());
                        
                        // Gửi Command tới Axon để kích hoạt Saga bước tiếp theo
                        commandGateway.send(CompletePaymentCommand.builder()
                                .paymentId(session.getClientReferenceId()) // Dùng OrderId làm TargetAggregateIdentifier cho luồng thanh toán
                                .transactionId(session.getPaymentIntent())
                                .build());
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
