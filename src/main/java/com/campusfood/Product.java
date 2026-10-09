package com.campusfood;

import java.math.BigDecimal;

/** A menu item that a restaurant sells. */
public class Product {

    private final String name;
    private final BigDecimal price;
    private boolean available;

    public Product(String name, BigDecimal price) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name must not be empty");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Product price must not be negative");
        }
        this.name = name;
        this.price = price;
        this.available = true;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return name + " (" + price + " TL)";
    }
}
