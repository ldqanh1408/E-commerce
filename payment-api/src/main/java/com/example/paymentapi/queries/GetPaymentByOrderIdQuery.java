package com.example.paymentapi.queries;

import lombok.Value;

@Value
public class GetPaymentByOrderIdQuery {
    String orderId;
}

