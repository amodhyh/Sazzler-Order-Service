package com.sazzler.ecommerce.sazzler_orderservice.Entity;

import jakarta.persistence.Entity;

@Entity
public class Product {

    enum Status {
        AVAILABLE,
        OUT_OF_STOCK,
        PROCESSING,
        SHIPPED
    }
    String name;
    String ID;
    String description;
    double price;
    Status status;


}
