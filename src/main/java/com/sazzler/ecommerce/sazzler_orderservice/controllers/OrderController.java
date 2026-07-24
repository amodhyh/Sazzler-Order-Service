package com.sazzler.ecommerce.sazzler_orderservice.controllers;

import com.sazzler.ecommerce.sazzler_api_def.order_service.DTO.OrderRequest;
import com.sazzler.ecommerce.sazzler_orderservice.Entity.Order;
import com.sazzler.ecommerce.sazzler_orderservice.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<com.sazzler.ecommerce.sazzler_api_def.order_service.DTO.OrderDTO> createOrder(@RequestHeader("X-User-Id") String userId, 
                                             @RequestBody OrderRequest orderRequest) {
        Order newOrder = orderService.createOrder(userId, orderRequest);
        
        com.sazzler.ecommerce.sazzler_api_def.order_service.DTO.OrderDTO dto = new com.sazzler.ecommerce.sazzler_api_def.order_service.DTO.OrderDTO(
                newOrder.getOrderId(),
                newOrder.getUserId(),
                newOrder.getItems().stream().map(item -> new com.sazzler.ecommerce.sazzler_api_def.order_service.DTO.OrderItemDTO(
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice()
                )).toList(),
                newOrder.getTotalPrice(),
                newOrder.getStatus(),
                newOrder.getCreatedAt(),
                newOrder.getUpdatedAt()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
}
