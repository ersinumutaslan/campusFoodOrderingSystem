package com.campusfood.restaurant;

import org.springframework.data.jpa.repository.JpaRepository;

/** Database access for restaurants. Spring Data JPA generates the implementation. */
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
}
