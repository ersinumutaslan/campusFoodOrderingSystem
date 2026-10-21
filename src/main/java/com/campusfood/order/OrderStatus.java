package com.campusfood.order;

/** The stages an order goes through, from placement to pickup. */
public enum OrderStatus {
    PLACED,      // student submitted the order
    PREPARING,   // restaurant staff started preparing it
    READY,       // ready for pickup
    COMPLETED,   // student picked it up
    CANCELLED;   // order was cancelled

    /** Whether an order in this status may move to the given next status. */
    public boolean canChangeTo(OrderStatus next) {
        return switch (this) {
            case PLACED -> next == PREPARING || next == CANCELLED;
            case PREPARING -> next == READY || next == CANCELLED;
            case READY -> next == COMPLETED;
            case COMPLETED, CANCELLED -> false;
        };
    }
}
