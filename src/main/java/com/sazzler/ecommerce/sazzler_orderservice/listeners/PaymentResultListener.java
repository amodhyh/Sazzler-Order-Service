package com.sazzler.ecommerce.sazzler_orderservice.listeners;

import com.sazzler.ecommerce.sazzler_api_def.payment_service.DTO.PaymentResultEvent;
import com.sazzler.ecommerce.sazzler_api_def.payment_service.DTO.PaymentStatus;
import com.sazzler.ecommerce.sazzler_orderservice.statemachine.OrderEvent;
import com.sazzler.ecommerce.sazzler_orderservice.services.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import com.sazzler.ecommerce.sazzler_orderservice.statemachine.OrderStateChangeInterceptor;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Mono;
import org.springframework.messaging.Message;
import com.sazzler.ecommerce.sazzler_orderservice.repository.OrderRepository;
import com.sazzler.ecommerce.sazzler_api_def.order_service.DTO.OrderStatus;
import com.sazzler.ecommerce.sazzler_orderservice.Entity.Order;
import jakarta.persistence.EntityNotFoundException;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentResultListener {

    private final OrderRepository orderRepository;
    private final StateMachineFactory<OrderStatus, OrderEvent> stateMachineFactory;
    private final OrderStateChangeInterceptor orderStateChangeInterceptor;

    @KafkaListener(topics = "payment-results", groupId = "sazzler-order-group")
    public void handlePaymentResult(PaymentResultEvent event) {
        log.info("Received PaymentResultEvent for order: {}, status: {}", event.orderId(), event.status());

        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + event.orderId()));

        OrderEvent stateEvent = (event.status() == PaymentStatus.APPROVED) ? 
                OrderEvent.PAYMENT_APPROVED : OrderEvent.PAYMENT_FAILED;

        sendEvent(event.orderId(), order.getStatus(), stateEvent);
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
        log.info("Triggered state transition {} for order {}", event, orderId);
    }
}
