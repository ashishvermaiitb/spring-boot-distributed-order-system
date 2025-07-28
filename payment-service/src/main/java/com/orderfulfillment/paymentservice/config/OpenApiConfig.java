package com.orderfulfillment.paymentservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Payment Service API",
                description = "Payment Service for Distributed Order Fulfillment System - Handles payment processing, status management, and order service integration",
                version = "v1.0",
                contact = @Contact(
                        name = "Order Fulfillment Team",
                        email = "support@orderfulfillment.com"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8082", description = "Local Development Server"),
                @Server(url = "http://payment-service:8082", description = "Docker Environment")
        }
)
public class OpenApiConfig {
}