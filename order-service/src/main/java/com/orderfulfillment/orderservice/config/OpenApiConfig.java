package com.orderfulfillment.orderservice.config;

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
    public OpenAPI orderServiceOpenAPI() {
        Server railwayServer = new Server()
                .url("https://order-service.up.railway.app") //or can use in the way used in customer service
                .description("Railway Production");

        Server localServer = new Server()
                .url("http://localhost:8080")
                .description("Local Development");

        Contact contact = new Contact()
                .name("Order Fulfillment Team")
                .email("support@orderfulfillment.com");

        Info info = new Info()
                .title("Order Service API")
                .version("v1.0")
                .description("Order Service for Distributed Order Fulfillment System")
                .contact(contact);

        return new OpenAPI()
                .info(info)
                .servers(List.of(railwayServer, localServer)); // Railway URL first as default
    }
}