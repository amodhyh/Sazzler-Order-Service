package com.sazzler.ecommerce.sazzler_orderservice.statemachine;

public enum OrderEvent {
    PAYMENT_APPROVED,
    PAYMENT_FAILED,
    DISPATCH,
    DELIVER,
    CANCEL
}
