package com.sazzler.ecommerce.sazzler_orderservice.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    enum Status {
        AVAILABLE,
        OUT_OF_STOCK,
        PROCESSING,
        SHIPPED
    }
    String name;
    @Id
    Integer ID;
    String description;
    double price;
    Status status;


}
