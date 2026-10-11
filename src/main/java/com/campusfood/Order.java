package com.campusfood;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A student's order: several items and a status. */
public class Order {

    private final List<OrderItem> items = new ArrayList<>();
    private OrderStatus status = OrderStatus.PLACED;

    public void addItem(OrderItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Item must not be null");
        }
        items.add(item);
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status must not be null");
        }
        this.status = status;
    }

    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : items) {
            total = total.add(item.getLineTotal());
        }
        return total;
    }
}
