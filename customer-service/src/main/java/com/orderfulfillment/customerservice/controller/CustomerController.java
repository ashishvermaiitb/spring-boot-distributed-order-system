package com.orderfulfillment.customerservice.controller;

import com.orderfulfillment.customerservice.dto.CustomerRequestDto;
import com.orderfulfillment.customerservice.dto.CustomerResponseDto;
import com.orderfulfillment.customerservice.service.CustomerService;
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
@RequestMapping("/api/v1/customers")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Customer Management", description = "APIs for managing customers")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    @Operation(summary = "Create a new customer", description = "Creates a new customer with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Customer created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Customer already exists with the provided email")
    })
    public ResponseEntity<CustomerResponseDto> createCustomer(
            @Valid @RequestBody CustomerRequestDto customerRequestDto) {

        logger.info("Received request to create customer with email: {}", customerRequestDto.getEmail());

        CustomerResponseDto createdCustomer = customerService.createCustomer(customerRequestDto);

        logger.info("Customer created successfully with ID: {}", createdCustomer.getId());
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }

    @GetMapping("/{customerId}")
    @Operation(summary = "Get customer by ID", description = "Retrieves a customer by their unique ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer found"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<CustomerResponseDto> getCustomerById(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable Long customerId) {

        logger.info("Received request to get customer with ID: {}", customerId);

        CustomerResponseDto customer = customerService.getCustomerById(customerId);

        logger.info("Customer retrieved successfully with ID: {}", customerId);
        return ResponseEntity.ok(customer);
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get customer by email", description = "Retrieves a customer by their email address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer found"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<CustomerResponseDto> getCustomerByEmail(
            @Parameter(description = "Customer email", required = true)
            @PathVariable String email) {

        logger.info("Received request to get customer with email: {}", email);

        CustomerResponseDto customer = customerService.getCustomerByEmail(email);

        logger.info("Customer retrieved successfully with email: {}", email);
        return ResponseEntity.ok(customer);
    }

    @GetMapping
    @Operation(summary = "Get all customers", description = "Retrieves a list of all customers")
    @ApiResponse(responseCode = "200", description = "Customers retrieved successfully")
    public ResponseEntity<List<CustomerResponseDto>> getAllCustomers() {

        logger.info("Received request to get all customers");

        List<CustomerResponseDto> customers = customerService.getAllCustomers();

        logger.info("Retrieved {} customers", customers.size());
        return ResponseEntity.ok(customers);
    }

    @PutMapping("/{customerId}")
    @Operation(summary = "Update customer", description = "Updates an existing customer with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "409", description = "Email already exists for another customer")
    })
    public ResponseEntity<CustomerResponseDto> updateCustomer(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable Long customerId,
            @Valid @RequestBody CustomerRequestDto customerRequestDto) {

        logger.info("Received request to update customer with ID: {}", customerId);

        CustomerResponseDto updatedCustomer = customerService.updateCustomer(customerId, customerRequestDto);

        logger.info("Customer updated successfully with ID: {}", customerId);
        return ResponseEntity.ok(updatedCustomer);
    }

    @DeleteMapping("/{customerId}")
    @Operation(summary = "Delete customer", description = "Deletes a customer by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Customer deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<Void> deleteCustomer(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable Long customerId) {

        logger.info("Received request to delete customer with ID: {}", customerId);

        customerService.deleteCustomer(customerId);

        logger.info("Customer deleted successfully with ID: {}", customerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{customerId}/exists")
    @Operation(summary = "Check if customer exists", description = "Checks if a customer exists by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Check completed"),
    })
    public ResponseEntity<Boolean> existsById(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable Long customerId) {

        logger.info("Received request to check if customer exists with ID: {}", customerId);

        boolean exists = customerService.existsById(customerId);

        logger.info("Customer exists check for ID {}: {}", customerId, exists);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Simple health check endpoint")
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Customer Service is running!");
    }
}