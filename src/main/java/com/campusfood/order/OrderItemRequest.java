package com.campusfood.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** One line of an incoming order: which product and how many. */
public record OrderItemRequest(
        @NotNull Long productId,
        @Min(value = 1, message = "must be at least 1") int quantity) {
}
