package com.sazzler.ecommerce.sazzler_orderservice.consumers;

import com.sazzler.ecommerce.api_def.product_service.DTO.ProductEvent;
import com.sazzler.ecommerce.sazzler_orderservice.services.ProductSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component // Or @Service
@RequiredArgsConstructor
@Slf4j
public class ProductEventConsumer {

    private final ProductSyncService productSyncService; // Call a specific service

    @KafkaListener(topics = "${spring.kafka.topic.name}", groupId = "order-service-group")
    public void handleProductUpdate(ProductEvent event) {
        log.info("Received Kafka event for Product: {}", event.productId());

        // Pass the work to the service layer
        productSyncService.updateProductCache(event);
    }
}