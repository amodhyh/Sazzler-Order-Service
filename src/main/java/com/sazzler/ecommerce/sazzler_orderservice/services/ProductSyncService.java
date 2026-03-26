package com.sazzler.ecommerce.sazzler_orderservice.services;


import com.sazzler.ecommerce.api_def.product_service.DTO.ProductEvent;
import com.sazzler.ecommerce.sazzler_orderservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductSyncService {
    private final ProductRepository repository;

    @Transactional
    public void updateProductCache(ProductEvent event) {
        // Business logic: Update local DB, handle price changes, etc.
    }
}