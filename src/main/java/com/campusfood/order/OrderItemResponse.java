package com.campusfood.order;

import java.math.BigDecimal;

/** The JSON shape of one order line returned by the API. */
public record OrderItemResponse(String product, int quantity, BigDecimal unitPrice, BigDecimal lineTotal) {

    static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(
                item.getProduct().getName(), item.getQuantity(), item.getUnitPrice(), item.getLineTotal());
    }
}
