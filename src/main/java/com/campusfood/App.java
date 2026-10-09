package com.campusfood;

import java.math.BigDecimal;

public class App {
    public static void main(String[] args) throws Exception {
        // Why not double for money?
        System.out.println("double:     0.1 + 0.2 = " + (0.1 + 0.2));
        System.out.println("BigDecimal: 0.1 + 0.2 = " + new BigDecimal("0.1").add(new BigDecimal("0.2")));

        Product wrap = new Product("Chicken Wrap", new BigDecimal("85.50"));
        System.out.println("Product: " + wrap);
        System.out.println("Available? " + wrap.isAvailable());

        wrap.setAvailable(false);
        System.out.println("Available after sold out? " + wrap.isAvailable());

        try {
            new Product("Free Lunch", new BigDecimal("-5"));
        } catch (IllegalArgumentException e) {
            System.out.println("Rejected: " + e.getMessage());
        }
    }
}
