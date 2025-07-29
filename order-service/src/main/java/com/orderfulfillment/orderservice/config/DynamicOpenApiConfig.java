package com.orderfulfillment.orderservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class DynamicOpenApiConfig {

    @Bean
    public OpenAPI orderServiceOpenAPI() {
        List<Server> servers = new ArrayList<>();

        // Try to get current request to determine the base URL
        try {
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attr.getRequest();
            String scheme = request.getScheme();
            String serverName = request.getServerName();
            int serverPort = request.getServerPort();

            String baseUrl;
            if ((scheme.equals("http") && serverPort == 80) || (scheme.equals("https") && serverPort == 443)) {
                baseUrl = scheme + "://" + serverName;
            } else {
                baseUrl = scheme + "://" + serverName + ":" + serverPort;
            }

            Server currentServer = new Server()
                    .url(baseUrl)
                    .description("Current Environment");
            servers.add(currentServer);

        } catch (Exception e) {
            // Fallback servers if we can't determine current request
            Server railwayServer = new Server()
                    .url("https://order-service.up.railway.app")
                    .description("Railway Production Environment");

            Server localServer = new Server()
                    .url("http://localhost:8080")
                    .description("Local Development Server");

            servers.add(railwayServer);
            servers.add(localServer);
        }

        Contact contact = new Contact()
                .name("Order Fulfillment Team")
                .email("support@orderfulfillment.com");

        Info info = new Info()
                .title("Order Service API")
                .version("v1.0")
                .description("Order Service for Distributed Order Fulfillment System - Orchestrates the entire order fulfillment process")
                .contact(contact);

        return new OpenAPI()
                .info(info)
                .servers(servers);
    }
}