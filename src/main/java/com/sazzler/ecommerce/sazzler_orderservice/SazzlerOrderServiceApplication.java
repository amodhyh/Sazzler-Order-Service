package com.sazzler.ecommerce.sazzler_orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SazzlerOrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SazzlerOrderServiceApplication.class, args);
    }

}
