package com.campusfood;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class OrderTest {

    @Test
    void totalIsSumOfAllLineTotals() {
        Product wrap = new Product("Chicken Wrap", new BigDecimal("85.50"));
        Product ayran = new Product("Ayran", new BigDecimal("20.00"));
        Order order = new Order();
        order.addItem(new OrderItem(wrap, 2));
        order.addItem(new OrderItem(ayran, 3));

        BigDecimal total = order.getTotal();

        assertEquals(new BigDecimal("231.00"), total);
    }

    @Test
    void emptyOrderTotalIsZero() {
        Order order = new Order();

        assertEquals(BigDecimal.ZERO, order.getTotal());
    }

    @Test
    void newOrderStartsAsPlaced() {
        Order order = new Order();

        assertEquals(OrderStatus.PLACED, order.getStatus());
    }
}
