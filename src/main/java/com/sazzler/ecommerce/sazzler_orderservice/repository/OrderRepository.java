package com.sazzler.ecommerce.sazzler_orderservice.repository;

import com.sazzler.ecommerce.sazzler_orderservice.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
}
