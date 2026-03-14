package com.example.paymentservice.coreapi.queries;

import lombok.Value;

@Value
public class GetPaymentByOrderIdQuery {
    String orderId;
}

