package com.orderfulfillment.paymentservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/debug")
public class DebugController {

    @Autowired
    private Environment environment;

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getDebugInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("service", "payment-service");
        info.put("status", "running");
        info.put("port", environment.getProperty("server.port"));
        info.put("profile", environment.getProperty("spring.profiles.active"));
        info.put("java.version", System.getProperty("java.version"));
        info.put("available.processors", Runtime.getRuntime().availableProcessors());
        info.put("max.memory", Runtime.getRuntime().maxMemory() / (1024 * 1024) + " MB");

        return ResponseEntity.ok(info);
    }

    @GetMapping("/health-simple")
    public ResponseEntity<String> simpleHealth() {
        return ResponseEntity.ok("Payment Service Debug Health OK");
    }
}