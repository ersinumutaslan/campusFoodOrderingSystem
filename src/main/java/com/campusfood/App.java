package com.campusfood;

import java.math.BigDecimal;

public class App {
    public static void main(String[] args) throws Exception {
        Product wrap = new Product("Chicken Wrap", new BigDecimal("85.50"));
        Product ayran = new Product("Ayran", new BigDecimal("20.00"));

        OrderItem item1 = new OrderItem(wrap, 2);
        OrderItem item2 = new OrderItem(ayran, 3);

        System.out.println(item1);
        System.out.println(item2);

        try {
            new OrderItem(wrap, 0);
        } catch (IllegalArgumentException e) {
            System.out.println("Rejected: " + e.getMessage());
        }
    }
}
