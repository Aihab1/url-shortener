package com.url.shortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ShorteningServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShorteningServiceApplication.class, args);
    }

}
