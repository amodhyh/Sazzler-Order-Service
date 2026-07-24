package com.sazzler.ecommerce.sazzler_orderservice.services;

import com.sazzler.ecommerce.sazzler_api_def.order_service.DTO.OrderItemRequest;
import com.sazzler.ecommerce.sazzler_api_def.order_service.DTO.OrderRequest;
import com.sazzler.ecommerce.sazzler_api_def.order_service.DTO.OrderStatus;
import com.sazzler.ecommerce.sazzler_api_def.order_service.Exceptions.InsufficientProductDataException;
import com.sazzler.ecommerce.sazzler_orderservice.Entity.Order;
import com.sazzler.ecommerce.sazzler_orderservice.Entity.OrderItem;
import com.sazzler.ecommerce.sazzler_orderservice.Entity.Product;
import com.sazzler.ecommerce.sazzler_orderservice.repository.OrderRepository;
import com.sazzler.ecommerce.sazzler_orderservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Order createOrder(String userId, OrderRequest request) {
        log.info("Creating order for user: {}", userId);
        
        Order order = Order.builder()
                .userId(userId)
                .status(OrderStatus.PENDING)
                .items(new ArrayList<>())
                .totalPrice(BigDecimal.ZERO)
                .build();

        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.items()) {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new InsufficientProductDataException(
                            "Product data missing for ID: " + itemRequest.productId()));

            OrderItem orderItem = OrderItem.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .quantity(itemRequest.quantity())
                    .unitPrice(product.getPrice())
                    .build();

            order.addItem(orderItem);
            
            // Calculate total for this item
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.quantity()));
            totalPrice = totalPrice.add(itemTotal);
        }

        order.setTotalPrice(totalPrice);
        Order savedOrder = orderRepository.save(order);
        
        log.info("Order {} successfully created in PENDING state", savedOrder.getOrderId());
        
        return savedOrder;
    }
}
