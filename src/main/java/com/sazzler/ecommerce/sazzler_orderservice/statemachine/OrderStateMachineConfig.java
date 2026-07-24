package com.sazzler.ecommerce.sazzler_orderservice.statemachine;

import com.sazzler.ecommerce.sazzler_api_def.order_service.DTO.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

@Slf4j
@Configuration
@EnableStateMachineFactory
public class OrderStateMachineConfig extends EnumStateMachineConfigurerAdapter<OrderStatus, OrderEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<OrderStatus, OrderEvent> states) throws Exception {
        states.withStates()
                .initial(OrderStatus.PENDING)
                .states(EnumSet.allOf(OrderStatus.class))
                .end(OrderStatus.DELIVERED)
                .end(OrderStatus.CANCELLED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderStatus, OrderEvent> transitions) throws Exception {
        transitions
                .withExternal().source(OrderStatus.PENDING).target(OrderStatus.CONFIRMED).event(OrderEvent.PAYMENT_APPROVED)
                .and()
                .withExternal().source(OrderStatus.PENDING).target(OrderStatus.CANCELLED).event(OrderEvent.PAYMENT_FAILED)
                .and()
                .withExternal().source(OrderStatus.PENDING).target(OrderStatus.CANCELLED).event(OrderEvent.CANCEL)
                .and()
                .withExternal().source(OrderStatus.CONFIRMED).target(OrderStatus.SHIPPED).event(OrderEvent.DISPATCH)
                .and()
                .withExternal().source(OrderStatus.CONFIRMED).target(OrderStatus.CANCELLED).event(OrderEvent.CANCEL)
                .and()
                .withExternal().source(OrderStatus.SHIPPED).target(OrderStatus.DELIVERED).event(OrderEvent.DELIVER);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<OrderStatus, OrderEvent> config) throws Exception {
        config.withConfiguration()
                .autoStartup(true)
                .listener(new StateMachineListenerAdapter<>() {
                    @Override
                    public void stateChanged(State<OrderStatus, OrderEvent> from, State<OrderStatus, OrderEvent> to) {
                        if (from != null) {
                            log.info("Order State Changed from {} to {}", from.getId(), to.getId());
                        } else {
                            log.info("Order State Initialized to {}", to.getId());
                        }
                    }
                });
    }
}
