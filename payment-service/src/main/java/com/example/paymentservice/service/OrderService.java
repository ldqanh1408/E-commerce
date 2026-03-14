package com.example.paymentservice.service;

import com.stripe.model.checkout.Session;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    public void updateOrderToPaid(Session session) {
        // Implement logic to update order status
        System.out.println("Updating order for session: " + session.getId());
    }
}

