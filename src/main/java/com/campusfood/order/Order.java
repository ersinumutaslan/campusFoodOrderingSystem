package com.campusfood.order;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.campusfood.restaurant.Product;
import com.campusfood.restaurant.Restaurant;
import com.campusfood.user.User;

/** A student's order from one restaurant: several items and a status. Stored in the "orders" table. */
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PLACED;

    private Instant createdAt;

    /** Used by JPA to create objects from database rows. */
    protected Order() {
    }

    public Order(User customer, Restaurant restaurant) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer must not be null");
        }
        if (restaurant == null) {
            throw new IllegalArgumentException("Restaurant must not be null");
        }
        this.customer = customer;
        this.restaurant = restaurant;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public User getCustomer() {
        return customer;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public Instant getCreatedAt() {
        return createdAt;
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
        item.setOrder(this);
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
