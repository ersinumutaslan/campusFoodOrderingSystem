package com.campusfood.order;

/** The stages an order goes through, from placement to pickup. */
public enum OrderStatus {
    PLACED,      // student submitted the order
    PREPARING,   // restaurant staff started preparing it
    READY,       // ready for pickup
    COMPLETED,   // student picked it up
    CANCELLED    // order was cancelled
}
