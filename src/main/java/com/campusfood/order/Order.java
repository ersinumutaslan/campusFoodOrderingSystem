package com.campusfood.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.campusfood.restaurant.Product;
import com.campusfood.restaurant.Restaurant;
import com.campusfood.user.User;

/** A student's order from one restaurant: several items and a status. */
public class Order {

    private final User customer;
    private final Restaurant restaurant;
    private final List<OrderItem> items = new ArrayList<>();
    private OrderStatus status = OrderStatus.PLACED;

    public Order(User customer, Restaurant restaurant) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer must not be null");
        }
        if (restaurant == null) {
            throw new IllegalArgumentException("Restaurant must not be null");
        }
        this.customer = customer;
        this.restaurant = restaurant;
    }

    public User getCustomer() {
        return customer;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void addItem(OrderItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Item must not be null");
        }
        Product product = item.getProduct();
        if (!restaurant.getMenu().contains(product)) {
            throw new IllegalArgumentException(product.getName() + " is not on the menu of " + restaurant.getName());
        }
        if (!product.isAvailable()) {
            throw new IllegalArgumentException(product.getName() + " is not available");
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
