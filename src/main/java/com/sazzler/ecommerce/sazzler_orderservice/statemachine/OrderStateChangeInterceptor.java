package com.sazzler.ecommerce.sazzler_orderservice.statemachine;

import com.sazzler.ecommerce.sazzler_api_def.order_service.DTO.OrderStatus;
import com.sazzler.ecommerce.sazzler_orderservice.Entity.Order;
import com.sazzler.ecommerce.sazzler_orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderStateChangeInterceptor extends StateMachineInterceptorAdapter<OrderStatus, OrderEvent> {

    private final OrderRepository orderRepository;

    @Transactional
    @Override
    public void preStateChange(State<OrderStatus, OrderEvent> state, Message<OrderEvent> message,
                               Transition<OrderStatus, OrderEvent> transition, StateMachine<OrderStatus, OrderEvent> stateMachine,
                               StateMachine<OrderStatus, OrderEvent> rootStateMachine) {

        Optional.ofNullable(message)
                .flatMap(msg -> Optional.ofNullable((String) msg.getHeaders().get("ORDER_ID")))
                .ifPresent(orderId -> {
                    Order order = orderRepository.findById(orderId).orElseThrow();
                    order.setStatus(state.getId());
                    orderRepository.save(order);
                });
    }
}
