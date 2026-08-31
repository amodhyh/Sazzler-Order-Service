package com.sazzler.ecommerce.sazzler_orderservice.services;

import com.sazzler.ecommerce.sazzler_api_def.order_service.DTO.OrderDTO;
import com.sazzler.ecommerce.sazzler_api_def.order_service.DTO.OrderItemRequest;
import com.sazzler.ecommerce.sazzler_api_def.order_service.DTO.OrderRequest;
import com.sazzler.ecommerce.sazzler_api_def.order_service.DTO.OrderStatus;
import com.sazzler.ecommerce.sazzler_api_def.order_service.Exceptions.InsufficientProductDataException;
import com.sazzler.ecommerce.sazzler_api_def.order_service.Exceptions.OrderCancellationException;
import com.sazzler.ecommerce.sazzler_orderservice.Entity.Order;
import com.sazzler.ecommerce.sazzler_orderservice.Entity.OrderItem;
import com.sazzler.ecommerce.sazzler_orderservice.Entity.Product;
import com.sazzler.ecommerce.sazzler_orderservice.repository.OrderRepository;
import com.sazzler.ecommerce.sazzler_orderservice.repository.ProductRepository;

import com.sazzler.ecommerce.sazzler_orderservice.statemachine.OrderEvent;
import com.sazzler.ecommerce.sazzler_orderservice.statemachine.OrderStateChangeInterceptor;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Mono;
import org.springframework.messaging.Message;

import jakarta.persistence.EntityNotFoundException;
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
    private final StateMachineFactory<OrderStatus, OrderEvent> stateMachineFactory;
    private final OrderStateChangeInterceptor orderStateChangeInterceptor;

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
                    .productId(product.getID())
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
    @Transactional
    public void cancelOrder(String userId, String orderId) {

        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        if (existingOrder.getUserId().equals(userId)) {
            if (existingOrder.getStatus().equals(OrderStatus.SHIPPED)) {
                throw new OrderCancellationException("Order has already Shipped");
            } else if (existingOrder.getStatus().equals(OrderStatus.DELIVERED)) {
                throw new OrderCancellationException("Order has already Delivered");
            }

            sendEvent(orderId, existingOrder.getStatus(), OrderEvent.CANCEL);
            log.info("Order {} Cancellation event sent ", orderId);
        } else {
            log.info("Order {} is not Cancelled, ACCESS DENIED", orderId);
            throw new RuntimeException("Access denied for user: " + userId);
        }
    }

    private void sendEvent(String orderId, OrderStatus currentStatus, OrderEvent event) {
        StateMachine<OrderStatus, OrderEvent> sm = stateMachineFactory.getStateMachine(orderId);
        sm.stopReactively().block();

        sm.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                    sma.addStateMachineInterceptor(orderStateChangeInterceptor);
                    sma.resetStateMachineReactively(
                            new DefaultStateMachineContext<>(currentStatus, null, null, null)).block();
                });

        sm.startReactively().block();
        
        Message<OrderEvent> msg = MessageBuilder.withPayload(event)
                .setHeader("ORDER_ID", orderId)
                .build();
        
        sm.sendEvent(Mono.just(msg)).blockLast();
    }
    
    public List<Order> viewOrders(String userId) {
        log.info("Fetching orders for user: {}", userId);
        return orderRepository.findByUserId(userId);
    }
}
