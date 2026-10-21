package com.campusfood.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.campusfood.restaurant.Product;
import com.campusfood.restaurant.Restaurant;
import com.campusfood.user.Role;
import com.campusfood.user.User;

class OrderTest {

    private User student;
    private Restaurant grill;
    private Product wrap;
    private Product ayran;

    @BeforeEach
    void setUp() {
        student = new User("Ayşe Yılmaz", "ayse@campus.edu", Role.STUDENT);
        grill = new Restaurant("Campus Grill");
        wrap = new Product("Chicken Wrap", new BigDecimal("85.50"));
        ayran = new Product("Ayran", new BigDecimal("20.00"));
        grill.addProduct(wrap);
        grill.addProduct(ayran);
    }

    @Test
    void totalIsSumOfAllLineTotals() {
        Order order = new Order(student, grill);
        order.addItem(new OrderItem(wrap, 2));
        order.addItem(new OrderItem(ayran, 3));

        BigDecimal total = order.getTotal();

        assertEquals(new BigDecimal("231.00"), total);
    }

    @Test
    void emptyOrderTotalIsZero() {
        Order order = new Order(student, grill);

        assertEquals(BigDecimal.ZERO, order.getTotal());
    }

    @Test
    void newOrderStartsAsPlaced() {
        Order order = new Order(student, grill);

        assertEquals(OrderStatus.PLACED, order.getStatus());
    }

    @Test
    void orderKnowsCustomerAndRestaurant() {
        Order order = new Order(student, grill);

        assertEquals(student, order.getCustomer());
        assertEquals(grill, order.getRestaurant());
    }

    @Test
    void productFromAnotherRestaurantIsRejected() {
        Product pizza = new Product("Pizza", new BigDecimal("150.00"));
        Order order = new Order(student, grill);

        assertThrows(IllegalArgumentException.class, () -> order.addItem(new OrderItem(pizza, 1)));
    }

    @Test
    void soldOutProductIsRejected() {
        wrap.setAvailable(false);
        Order order = new Order(student, grill);

        assertThrows(IllegalArgumentException.class, () -> order.addItem(new OrderItem(wrap, 1)));
    }

    @Test
    void statusMovesForwardThroughTheKitchen() {
        Order order = new Order(student, grill);

        order.changeStatus(OrderStatus.PREPARING);
        order.changeStatus(OrderStatus.READY);
        order.changeStatus(OrderStatus.COMPLETED);

        assertEquals(OrderStatus.COMPLETED, order.getStatus());
    }

    @Test
    void completedOrderCannotGoBackToPreparing() {
        Order order = new Order(student, grill);
        order.changeStatus(OrderStatus.PREPARING);
        order.changeStatus(OrderStatus.READY);
        order.changeStatus(OrderStatus.COMPLETED);

        assertThrows(IllegalStateException.class, () -> order.changeStatus(OrderStatus.PREPARING));
        assertEquals(OrderStatus.COMPLETED, order.getStatus());
    }

    @Test
    void placedOrderCannotSkipToReady() {
        Order order = new Order(student, grill);

        assertThrows(IllegalStateException.class, () -> order.changeStatus(OrderStatus.READY));
    }

    @Test
    void readyOrderCannotBeCancelled() {
        Order order = new Order(student, grill);
        order.changeStatus(OrderStatus.PREPARING);
        order.changeStatus(OrderStatus.READY);

        assertThrows(IllegalStateException.class, () -> order.changeStatus(OrderStatus.CANCELLED));
    }

    @Test
    void orderWithoutCustomerIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Order(null, grill));
    }
}
