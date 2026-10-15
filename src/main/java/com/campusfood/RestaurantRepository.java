package com.campusfood;

import java.util.List;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

/** Reads restaurants from the database. The only class that knows the SQL for restaurants. */
@Repository
public class RestaurantRepository {

    private final JdbcClient jdbcClient;

    public RestaurantRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<RestaurantResponse> findAll() {
        return jdbcClient.sql("SELECT id, name FROM restaurants ORDER BY id")
                .query(RestaurantResponse.class)
                .list();
    }
}
