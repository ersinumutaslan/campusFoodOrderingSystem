package com.campusfood.order;

/** One line of an incoming order: which product and how many. */
public record OrderItemRequest(Long productId, int quantity) {
}
