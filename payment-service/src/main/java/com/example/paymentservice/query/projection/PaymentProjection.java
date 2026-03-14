package com.example.paymentservice.query.projection;

import com.example.paymentservice.coreapi.queries.GetPaymentByOrderIdQuery;
import com.example.paymentservice.coreapi.queries.GetPaymentQuery;
import com.example.paymentservice.coreapi.queries.dto.PaymentDto;
import com.example.paymentservice.query.service.PaymentQueryService;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentProjection {

    private final PaymentQueryService paymentQueryService;

    @QueryHandler
    public PaymentDto handle(GetPaymentQuery query) {
        return paymentQueryService.findByPaymentId(query.getPaymentId());
    }

    @QueryHandler
    public PaymentDto handle(GetPaymentByOrderIdQuery query) {
        return paymentQueryService.findByOrderId(query.getOrderId());
    }
}

