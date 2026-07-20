package com.sazzler.ecommerce.sazzler_orderservice.repository;

import com.sazzler.ecommerce.sazzler_orderservice.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer>{
    

}
