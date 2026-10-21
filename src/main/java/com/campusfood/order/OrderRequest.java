package com.campusfood.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * The JSON a client sends to place an order. There is no customer field:
 * the customer is always the logged-in user. The annotations are checked before the controller runs.
 */
public record OrderRequest(
        @NotNull Long restaurantId,
        @NotEmpty List<@Valid OrderItemRequest> items) {
}
