package com.sazzler.ecommerce.sazzler_orderservice.Entity;

import java.math.BigDecimal;

// removed incorrect import jakarta.annotation.Generated; use jakarta.persistence.GeneratedValue instead
import jakarta.persistence.Entity;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import jakarta.persistence.*;


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
// Product service generate the ID and order service just maintains it
    @Id
    String ID;

    String name;

    
    String description;
    BigDecimal price;
    Status status;


}
