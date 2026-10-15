package com.campusfood.demo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/** Minimal JDBC example: read one restaurant's available menu from PostgreSQL. */
public class JdbcDemo {

    // Local development database from docker-compose.yml
    private static final String URL = "jdbc:postgresql://localhost:5433/campusfood";
    private static final String USER = "campusfood";
    private static final String PASSWORD = "campusfood";

    public static void main(String[] args) throws Exception {
        long restaurantId = 1;

        String sql = """
                SELECT p.name, p.price
                FROM products p
                WHERE p.restaurant_id = ? AND p.available = TRUE
                ORDER BY p.name
                """;

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, restaurantId);

            try (ResultSet rows = statement.executeQuery()) {
                System.out.println("Menu of restaurant " + restaurantId + ":");
                while (rows.next()) {
                    String name = rows.getString("name");
                    BigDecimal price = rows.getBigDecimal("price");
                    System.out.println("  " + name + " (" + price + " TL)");
                }
            }
        }
    }
}
