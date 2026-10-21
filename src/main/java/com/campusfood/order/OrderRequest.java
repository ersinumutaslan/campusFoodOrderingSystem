package com.campusfood.order;

import java.util.List;

/** The JSON a client sends to place an order. */
public record OrderRequest(Long customerId, Long restaurantId, List<OrderItemRequest> items) {
}
