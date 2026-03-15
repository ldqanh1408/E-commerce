package com.example.paymentservice.service;

import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    // Use default empty to avoid unresolved-placeholder errors during bean creation
    @Value("${stripe.api.key:}")
    private String stripeApiKey;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @PostConstruct
    public void init() {
        if (stripeApiKey == null || stripeApiKey.isBlank()) {
            log.warn("Stripe API key is not configured (stripe.api.key). Stripe operations will fail until it's provided.");
        }
        if (frontendUrl == null || frontendUrl.isBlank()) {
            log.warn("Frontend URL (app.frontend.url) is not configured; using default value.");
        }
        Stripe.apiKey = stripeApiKey;
    }

    public String createCheckoutSession(String orderId, Long amount) throws Exception {
        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(frontendUrl + "/payment/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(frontendUrl + "/payment/cancel")
                .setClientReferenceId(orderId) // Lưu orderId để đối soát khi Webhook gọi về
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("usd")
                                .setUnitAmount(amount) // Số tiền tính bằng cents (VD: 1000 = $10.00)
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName("Thanh toán đơn hàng #" + orderId)
                                        .build())
                                .build())
                        .build())
                .build();

        Session session = Session.create(params);
        return session.getUrl(); // Đây chính là "nơi" người dùng sẽ thanh toán
    }

    public void updateOrderToPaid(Session session) {
        System.out.println("Updating order for session: " + session.getId());
    }
}
