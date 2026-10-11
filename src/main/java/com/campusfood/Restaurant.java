package com.campusfood;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A campus restaurant and the products on its menu. */
public class Restaurant {

    private final String name;
    private final List<Product> menu = new ArrayList<>();

    public Restaurant(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Restaurant name must not be empty");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product must not be null");
        }
        menu.add(product);
    }

    public List<Product> getMenu() {
        return Collections.unmodifiableList(menu);
    }

    public List<Product> getAvailableProducts() {
        List<Product> available = new ArrayList<>();
        for (Product product : menu) {
            if (product.isAvailable()) {
                available.add(product);
            }
        }
        return available;
    }

    @Override
    public String toString() {
        return name;
    }
}
