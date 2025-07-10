package com.sazzler.ecommerce.sazzler_orderservice;

import org.springframework.boot.SpringApplication;

public class TestSazzlerOrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(SazzlerOrderServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
