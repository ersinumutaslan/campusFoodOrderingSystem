package com.campusfood.order;

import org.springframework.data.jpa.repository.JpaRepository;

/** Database access for orders. Saving an order also saves its items (cascade). */
public interface OrderRepository extends JpaRepository<Order, Long> {
}
