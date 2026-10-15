package com.campusfood.restaurant;

import java.util.List;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

/** Reads products from the database. */
@Repository
public class ProductRepository {

    private final JdbcClient jdbcClient;

    public ProductRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<ProductResponse> findAvailableByRestaurantId(long restaurantId) {
        return jdbcClient.sql("""
                        SELECT id, name, price
                        FROM products
                        WHERE restaurant_id = ? AND available = TRUE
                        ORDER BY name
                        """)
                .param(restaurantId)
                .query(ProductResponse.class)
                .list();
    }
}
