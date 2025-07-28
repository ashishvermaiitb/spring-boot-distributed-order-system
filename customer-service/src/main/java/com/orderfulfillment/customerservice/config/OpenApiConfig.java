package com.orderfulfillment.customerservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customerServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Customer Service API")
                        .description("REST API for Customer Service in Distributed Order Fulfillment System")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Order Fulfillment Team")
                                .email("support@orderfulfillment.com")))
                .servers(Arrays.asList(
                        new Server().url("http://localhost:8081").description("Local Development Server"),
                        new Server().url("http://customer-service:8081").description("Docker Environment")
                ));
    }
}