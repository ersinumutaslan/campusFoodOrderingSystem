package com.campusfood;

import java.math.BigDecimal;

public class App {
    public static void main(String[] args) throws Exception {
        Product wrap = new Product("Chicken Wrap", new BigDecimal("85.50"));
        Product ayran = new Product("Ayran", new BigDecimal("20.00"));

        Order order = new Order();
        order.addItem(new OrderItem(wrap, 2));
        order.addItem(new OrderItem(ayran, 3));

        System.out.println("Order (" + order.getStatus() + "):");
        for (OrderItem item : order.getItems()) {
            System.out.println("  " + item);
        }
        System.out.println("Total: " + order.getTotal() + " TL");

        order.setStatus(OrderStatus.PREPARING);
        System.out.println("Status now: " + order.getStatus());
    }
}
