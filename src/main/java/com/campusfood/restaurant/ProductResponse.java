package com.campusfood.restaurant;

import java.math.BigDecimal;

/** The JSON shape of a menu item returned by the API. */
public record ProductResponse(Long id, String name, BigDecimal price) {

    static ProductResponse from(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getPrice());
    }
}
