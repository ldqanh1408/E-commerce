package com.example.paymentservice.query.projection;

import com.example.paymentapi.queries.GetPaymentByOrderIdQuery;
import com.example.paymentapi.queries.GetPaymentQuery;
import com.example.paymentapi.queries.dto.PaymentDto;
import com.example.paymentservice.query.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentProjection {

    private final PaymentService paymentService;

    @QueryHandler
    public PaymentDto handle(GetPaymentQuery query) {
        return paymentService.findByPaymentId(query.getPaymentId());
    }

    @QueryHandler
    public PaymentDto handle(GetPaymentByOrderIdQuery query) {
        return paymentService.findByOrderId(query.getOrderId());
    }
}
