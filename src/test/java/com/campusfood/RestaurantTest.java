package com.campusfood;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class RestaurantTest {

    @Test
    void availableProductsExcludeSoldOutItems() {
        Restaurant restaurant = new Restaurant("Campus Grill");
        Product wrap = new Product("Chicken Wrap", new BigDecimal("85.50"));
        Product soup = new Product("Lentil Soup", new BigDecimal("40.00"));
        restaurant.addProduct(wrap);
        restaurant.addProduct(soup);

        soup.setAvailable(false);

        assertEquals(List.of(wrap), restaurant.getAvailableProducts());
        assertEquals(2, restaurant.getMenu().size());
    }

    @Test
    void emptyNameIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Restaurant(""));
    }
}
