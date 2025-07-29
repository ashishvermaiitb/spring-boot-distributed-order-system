package com.orderfulfillment.customerservice.config;

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
    public OpenAPI customerServiceOpenAPI() {
        Server currentServer = new Server()
                .url("/") // Use relative URL - this will use current domain
                .description("Current Environment");

        Server localServer = new Server()
                .url("http://localhost:8081")
                .description("Local Development Server");

        Contact contact = new Contact()
                .name("Order Fulfillment Team")
                .email("support@orderfulfillment.com");

        Info info = new Info()
                .title("Customer Service API")
                .version("v1.0")
                .description("Customer Service for Distributed Order Fulfillment System - Manages customer data, validation, and provides customer information to other services")
                .contact(contact);

        return new OpenAPI()
                .info(info)
                .servers(List.of(currentServer, localServer)); // Relative URL first
    }
}