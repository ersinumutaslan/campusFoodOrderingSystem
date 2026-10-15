package com.campusfood.restaurant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A campus restaurant and the products on its menu. Stored in the "restaurants" table. */
@Entity
@Table(name = "restaurants")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "restaurant")
    private List<Product> menu = new ArrayList<>();

    /** Used by JPA to create objects from database rows. */
    protected Restaurant() {
    }

    public Restaurant(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Restaurant name must not be empty");
        }
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product must not be null");
        }
        menu.add(product);
        product.setRestaurant(this);
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
