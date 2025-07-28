package com.orderfulfillment.paymentservice.controller;

import com.orderfulfillment.paymentservice.dto.PaymentRequestDto;
import com.orderfulfillment.paymentservice.dto.PaymentResponseDto;
import com.orderfulfillment.paymentservice.enums.PaymentStatus;
import com.orderfulfillment.paymentservice.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payment Management", description = "APIs for managing payments")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @Operation(summary = "Create a new payment", description = "Creates a new payment for an order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payment created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<PaymentResponseDto> createPayment(
            @Valid @RequestBody PaymentRequestDto paymentRequestDto) {

        logger.info("Received request to create payment for order ID: {}", paymentRequestDto.getOrderId());

        PaymentResponseDto createdPayment = paymentService.createPayment(paymentRequestDto);

        logger.info("Payment created successfully with ID: {}", createdPayment.getId());
        return new ResponseEntity<>(createdPayment, HttpStatus.CREATED);
    }

    @GetMapping("/{paymentId}")
    @Operation(summary = "Get payment by ID", description = "Retrieves a payment by its unique ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment found"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    public ResponseEntity<PaymentResponseDto> getPaymentById(
            @Parameter(description = "Payment ID", required = true)
            @PathVariable Long paymentId) {

        logger.info("Received request to get payment with ID: {}", paymentId);

        PaymentResponseDto payment = paymentService.getPaymentById(paymentId);

        return ResponseEntity.ok(payment);
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get payment by order ID", description = "Retrieves a payment by order ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment found"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    public ResponseEntity<PaymentResponseDto> getPaymentByOrderId(
            @Parameter(description = "Order ID", required = true)
            @PathVariable Long orderId) {

        logger.info("Received request to get payment for order ID: {}", orderId);

        PaymentResponseDto payment = paymentService.getPaymentByOrderId(orderId);

        return ResponseEntity.ok(payment);
    }

    @GetMapping
    @Operation(summary = "Get all payments", description = "Retrieves a list of all payments")
    @ApiResponse(responseCode = "200", description = "Payments retrieved successfully")
    public ResponseEntity<List<PaymentResponseDto>> getAllPayments() {

        logger.info("Received request to get all payments");

        List<PaymentResponseDto> payments = paymentService.getAllPayments();

        logger.info("Retrieved {} payments", payments.size());
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get payments by status", description = "Retrieves payments filtered by status")
    @ApiResponse(responseCode = "200", description = "Payments retrieved successfully")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByStatus(
            @Parameter(description = "Payment status", required = true)
            @PathVariable PaymentStatus status) {

        logger.info("Received request to get payments with status: {}", status);

        List<PaymentResponseDto> payments = paymentService.getPaymentsByStatus(status);

        logger.info("Retrieved {} payments with status: {}", payments.size(), status);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/order/{orderId}/exists")
    @Operation(summary = "Check if payment exists for order", description = "Checks if a payment exists for the given order ID")
    @ApiResponse(responseCode = "200", description = "Check completed")
    public ResponseEntity<Boolean> existsByOrderId(
            @Parameter(description = "Order ID", required = true)
            @PathVariable Long orderId) {

        logger.info("Received request to check if payment exists for order ID: {}", orderId);

        boolean exists = paymentService.existsByOrderId(orderId);

        logger.info("Payment exists for order ID {}: {}", orderId, exists);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Simple health check endpoint")
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Payment Service is running!");
    }
}