package com.orderfulfillment.paymentservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI paymentServiceOpenAPI() {
        Server localServer = new Server()
                .url("http://localhost:8082")
                .description("Local Development Server");

        Server dockerServer = new Server()
                .url("http://payment-service:8082")
                .description("Docker Environment");

        Contact contact = new Contact()
                .name("Order Fulfillment Team")
                .email("support@orderfulfillment.com");

        Info info = new Info()
                .title("Payment Service API")
                .version("v1.0")
                .description("Payment Service for Distributed Order Fulfillment System - Handles payment processing, status management, and order service integration with automated scheduled processing")
                .contact(contact);

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer, dockerServer));
    }
}