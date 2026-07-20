package com.sazzler.ecommerce.sazzler_orderservice.services;


import com.sazzler.ecommerce.sazzler_api_def.product_service.DTO.ProductEvent;
import com.sazzler.ecommerce.sazzler_api_def.product_service.DTO.ProductEventType;
import com.sazzler.ecommerce.sazzler_orderservice.Entity.Product;
import com.sazzler.ecommerce.sazzler_orderservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Mutate the database cache based on the incoming event's state transition.
@Service
@RequiredArgsConstructor
public class ProductSyncService {
    private final ProductRepository repository;

    @Transactional
    public void updateProductCache(ProductEvent event) {
        ProductEventType eventType = event.eventType();
        switch (eventType) {
            case CREATED, UPDATED -> {
                Product product = Product.builder()
                        .ID(event.productId())
                        .name(event.name())
                        .price(event.price())
                        .build();
                repository.save(product);
            }
            case DELETED -> {
                repository.deleteById(event.productId());
            }
        }
    }
}