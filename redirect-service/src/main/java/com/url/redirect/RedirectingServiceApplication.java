package com.url.redirect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class RedirectingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedirectingServiceApplication.class, args);
    }

}
