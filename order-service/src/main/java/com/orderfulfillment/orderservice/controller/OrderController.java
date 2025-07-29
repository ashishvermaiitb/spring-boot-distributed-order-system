package com.orderfulfillment.orderservice.controller;

import com.orderfulfillment.orderservice.dto.CompleteOrderDetailsDto;
import com.orderfulfillment.orderservice.dto.OrderRequestDto;
import com.orderfulfillment.orderservice.dto.OrderResponseDto;
import com.orderfulfillment.orderservice.enums.OrderStatus;
import com.orderfulfillment.orderservice.service.OrderService;
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
import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Order Management", description = "APIs for managing orders and orchestrating the fulfillment process")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "Create a new order", description = "Creates a new order and initiates the fulfillment process")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "503", description = "External service unavailable")
    })
    public ResponseEntity<OrderResponseDto> createOrder(@Valid @RequestBody OrderRequestDto orderRequestDto) {
        logger.info("Received request to create order for customer: {}", orderRequestDto.getCustomerId());

        OrderResponseDto createdOrder = orderService.createOrder(orderRequestDto);

        logger.info("Order created successfully with ID: {}", createdOrder.getId());
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID", description = "Retrieves an order by its unique ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long orderId) {
        logger.info("Received request to get order with ID: {}", orderId);

        OrderResponseDto order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    @Operation(summary = "Get all orders", description = "Retrieves a list of all orders")
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        logger.info("Received request to get all orders");

        List<OrderResponseDto> orders = orderService.getAllOrders();
        logger.info("Retrieved {} orders", orders.size());
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get orders by customer ID", description = "Retrieves all orders for a specific customer")
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByCustomerId(@PathVariable Long customerId) {
        logger.info("Received request to get orders for customer: {}", customerId);

        List<OrderResponseDto> orders = orderService.getOrdersByCustomerId(customerId);
        logger.info("Retrieved {} orders for customer: {}", orders.size(), customerId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get orders by status", description = "Retrieves orders filtered by status")
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByStatus(@PathVariable OrderStatus status) {
        logger.info("Received request to get orders with status: {}", status);

        List<OrderResponseDto> orders = orderService.getOrdersByStatus(status);
        logger.info("Retrieved {} orders with status: {}", orders.size(), status);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}/complete-details")
    @Operation(summary = "Get complete order details",
            description = "Retrieves complete order details including customer and payment information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Complete order details retrieved"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<CompleteOrderDetailsDto> getCompleteOrderDetails(@PathVariable Long orderId) {
        logger.info("Received request to get complete details for order: {}", orderId);

        CompleteOrderDetailsDto completeDetails = orderService.getCompleteOrderDetails(orderId);
        return ResponseEntity.ok(completeDetails);
    }

    @PutMapping("/{orderId}/status")
    @Operation(summary = "Update order status", description = "Updates the status of an order (used by payment service)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid status transition"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<Void> updateOrderStatus(@PathVariable Long orderId,
                                                  @RequestBody Map<String, Object> statusUpdate) {
        logger.info("Received request to update order {} status", orderId);

        OrderStatus status = OrderStatus.valueOf((String) statusUpdate.get("status"));
        Long paymentId = statusUpdate.get("paymentId") != null ?
                Long.valueOf(statusUpdate.get("paymentId").toString()) : null;

        orderService.updateOrderStatus(orderId, status, paymentId);

        logger.info("Order {} status updated to {}", orderId, status);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel order", description = "Cancels an order with a reason")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
            @ApiResponse(responseCode = "400", description = "Cannot cancel order in current state"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId,
                                            @RequestBody Map<String, String> cancellationRequest) {
        logger.info("Received request to cancel order: {}", orderId);

        String reason = cancellationRequest.get("reason");
        orderService.cancelOrder(orderId, reason);

        logger.info("Order {} cancelled successfully", orderId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Simple health check endpoint")
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Order Service is running!");
    }
}