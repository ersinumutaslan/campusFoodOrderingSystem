package com.campusfood.order;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Database access for orders. Saving an order also saves its items (cascade). */
public interface OrderRepository extends JpaRepository<Order, Long> {

    /** SELECT ... FROM orders JOIN users ... WHERE users.email = ? ORDER BY created_at DESC */
    List<Order> findByCustomerEmailOrderByCreatedAtDesc(String email);
}
