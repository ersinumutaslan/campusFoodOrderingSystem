package com.campusfood.order;

import jakarta.validation.constraints.NotNull;

/** The JSON a client sends to change an order's status, e.g. {"status": "PREPARING"}. */
public record StatusUpdateRequest(@NotNull OrderStatus status) {
}
