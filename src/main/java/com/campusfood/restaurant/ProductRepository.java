package com.campusfood.restaurant;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Database access for products. Spring Data JPA generates the implementation. */
public interface ProductRepository extends JpaRepository<Product, Long> {

    /** SELECT ... FROM products WHERE restaurant_id = ? AND available = TRUE ORDER BY name */
    List<Product> findByRestaurantIdAndAvailableTrueOrderByNameAsc(Long restaurantId);
}
