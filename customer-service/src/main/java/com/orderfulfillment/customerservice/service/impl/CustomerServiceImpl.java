package com.orderfulfillment.customerservice.service.impl;

import com.orderfulfillment.customerservice.dto.CustomerRequestDto;
import com.orderfulfillment.customerservice.dto.CustomerResponseDto;
import com.orderfulfillment.customerservice.entity.Customer;
import com.orderfulfillment.customerservice.exception.CustomerAlreadyExistsException;
import com.orderfulfillment.customerservice.exception.CustomerNotFoundException;
import com.orderfulfillment.customerservice.mapper.CustomerMapper;
import com.orderfulfillment.customerservice.repository.CustomerRepository;
import com.orderfulfillment.customerservice.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Override
    public CustomerResponseDto createCustomer(CustomerRequestDto customerRequestDto) {
        logger.info("Creating new customer with email: {}", customerRequestDto.getEmail());

        // Check if customer already exists with this email
        if (customerRepository.existsByEmail(customerRequestDto.getEmail())) {
            logger.error("Customer already exists with email: {}", customerRequestDto.getEmail());
            throw new CustomerAlreadyExistsException("Customer already exists with email: " + customerRequestDto.getEmail());
        }

        // Convert DTO to Entity
        Customer customer = customerMapper.toEntity(customerRequestDto);

        // Save customer
        Customer savedCustomer = customerRepository.save(customer);
        logger.info("Customer created successfully with ID: {}", savedCustomer.getId());

        // Convert Entity to Response DTO
        return customerMapper.toResponseDto(savedCustomer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponseDto getCustomerById(Long customerId) {
        logger.info("Fetching customer with ID: {}", customerId);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    logger.error("Customer not found with ID: {}", customerId);
                    return new CustomerNotFoundException("Customer not found with ID: " + customerId);
                });

        logger.info("Customer found with ID: {}", customerId);
        return customerMapper.toResponseDto(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponseDto getCustomerByEmail(String email) {
        logger.info("Fetching customer with email: {}", email);

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Customer not found with email: {}", email);
                    return new CustomerNotFoundException("Customer not found with email: " + email);
                });

        logger.info("Customer found with email: {}", email);
        return customerMapper.toResponseDto(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponseDto> getAllCustomers() {
        logger.info("Fetching all customers");

        List<Customer> customers = customerRepository.findAll();
        logger.info("Found {} customers", customers.size());

        return customers.stream()
                .map(customerMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponseDto updateCustomer(Long customerId, CustomerRequestDto customerRequestDto) {
        logger.info("Updating customer with ID: {}", customerId);

        Customer existingCustomer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    logger.error("Customer not found with ID: {}", customerId);
                    return new CustomerNotFoundException("Customer not found with ID: " + customerId);
                });

        // Check if email is being changed and if new email already exists
        if (!existingCustomer.getEmail().equals(customerRequestDto.getEmail())) {
            if (customerRepository.existsByEmail(customerRequestDto.getEmail())) {
                logger.error("Email already exists: {}", customerRequestDto.getEmail());
                throw new CustomerAlreadyExistsException("Email already exists: " + customerRequestDto.getEmail());
            }
        }

        // Update customer fields
        customerMapper.updateEntityFromDto(customerRequestDto, existingCustomer);

        // Save updated customer
        Customer updatedCustomer = customerRepository.save(existingCustomer);
        logger.info("Customer updated successfully with ID: {}", updatedCustomer.getId());

        return customerMapper.toResponseDto(updatedCustomer);
    }

    @Override
    public void deleteCustomer(Long customerId) {
        logger.info("Deleting customer with ID: {}", customerId);

        if (!customerRepository.existsById(customerId)) {
            logger.error("Customer not found with ID: {}", customerId);
            throw new CustomerNotFoundException("Customer not found with ID: " + customerId);
        }

        customerRepository.deleteById(customerId);
        logger.info("Customer deleted successfully with ID: {}", customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long customerId) {
        logger.debug("Checking if customer exists with ID: {}", customerId);
        boolean exists = customerRepository.existsById(customerId);
        logger.debug("Customer exists with ID {}: {}", customerId, exists);
        return exists;
    }
}