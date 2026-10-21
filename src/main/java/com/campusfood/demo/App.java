package com.campusfood.demo;

import java.math.BigDecimal;

import com.campusfood.order.Order;
import com.campusfood.order.OrderItem;
import com.campusfood.order.OrderStatus;
import com.campusfood.restaurant.Product;
import com.campusfood.restaurant.Restaurant;
import com.campusfood.user.Role;
import com.campusfood.user.User;

public class App {
    public static void main(String[] args) throws Exception {
        // 1. A restaurant with a menu
        Restaurant grill = new Restaurant("Campus Grill");
        Product wrap = new Product("Chicken Wrap", new BigDecimal("85.50"));
        Product soup = new Product("Lentil Soup", new BigDecimal("40.00"));
        Product ayran = new Product("Ayran", new BigDecimal("20.00"));
        grill.addProduct(wrap);
        grill.addProduct(soup);
        grill.addProduct(ayran);
        soup.setAvailable(false); // sold out today

        // 2. Users
        User student = new User("Ayşe Yılmaz", "ayse@campus.edu", Role.STUDENT);
        User staff = new User("Mehmet Demir", "mehmet@campus.edu", Role.STAFF);

        // 3. The student browses the menu
        System.out.println("=== " + grill.getName() + " menu ===");
        for (Product product : grill.getAvailableProducts()) {
            System.out.println("  " + product);
        }

        // 4. The student places an order
        Order order = new Order(student, grill);
        order.addItem(new OrderItem(wrap, 2));
        order.addItem(new OrderItem(ayran, 1));

        System.out.println();
        System.out.println("=== Order by " + order.getCustomer().getName()
                + " at " + order.getRestaurant().getName() + " ===");
        for (OrderItem item : order.getItems()) {
            System.out.println("  " + item);
        }
        System.out.println("  Total: " + order.getTotal() + " TL");
        System.out.println("  Status: " + order.getStatus());

        // The student tries to add the sold-out soup
        try {
            order.addItem(new OrderItem(soup, 1));
        } catch (IllegalArgumentException e) {
            System.out.println("  Rejected: " + e.getMessage());
        }

        // 5. Staff process the order; a student is not allowed to
        System.out.println();
        updateStatus(student, order, OrderStatus.READY);
        updateStatus(staff, order, OrderStatus.PREPARING);
        updateStatus(staff, order, OrderStatus.READY);
        updateStatus(staff, order, OrderStatus.COMPLETED);
    }

    private static void updateStatus(User user, Order order, OrderStatus newStatus) {
        if (user.getRole() != Role.STAFF) {
            System.out.println(user.getName() + " (" + user.getRole() + ") cannot change order status");
            return;
        }
        order.changeStatus(newStatus);
        System.out.println(user.getName() + " set order to " + order.getStatus());
    }
}
