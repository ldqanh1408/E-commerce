package com.example.orderservice.query.projection;


import com.example.orderapi.queries.FindAllOrdersQuery;
import com.example.orderapi.queries.FindOrderQuery;
import com.example.orderservice.query.dto.OrderDto;
import com.example.orderservice.query.entity.Order;
import com.example.orderservice.query.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderProjection {

    private final OrderService orderService;

    @QueryHandler
    public OrderDto handle(FindOrderQuery query) {
        return orderService.findById(query.getOrderId())
                .map(this::toDto)
                .orElse(null);
    }

    @QueryHandler
    public List<OrderDto> handle(FindAllOrdersQuery query) {
        return orderService.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private OrderDto toDto(Order order) {
        OrderDto orderDto = new OrderDto();
        BeanUtils.copyProperties(order, orderDto);
        return orderDto;
    }
}
