package com.campusfood.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/** The JSON shape of an order returned by the API. */
public record OrderResponse(
        Long id,
        String customer,
        String restaurant,
        OrderStatus status,
        List<OrderItemResponse> items,
        BigDecimal total,
        Instant createdAt) {

    static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomer().getName(),
                order.getRestaurant().getName(),
                order.getStatus(),
                order.getItems().stream().map(OrderItemResponse::from).toList(),
                order.getTotal(),
                order.getCreatedAt());
    }
}
