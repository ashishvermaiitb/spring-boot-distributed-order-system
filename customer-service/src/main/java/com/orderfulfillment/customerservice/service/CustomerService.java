package com.orderfulfillment.customerservice.service;

import com.orderfulfillment.customerservice.dto.CustomerRequestDto;
import com.orderfulfillment.customerservice.dto.CustomerResponseDto;

import java.util.List;

public interface CustomerService {

    CustomerResponseDto createCustomer(CustomerRequestDto customerRequestDto);

    CustomerResponseDto getCustomerById(Long customerId);

    CustomerResponseDto getCustomerByEmail(String email);

    List<CustomerResponseDto> getAllCustomers();

    CustomerResponseDto updateCustomer(Long customerId, CustomerRequestDto customerRequestDto);

    void deleteCustomer(Long customerId);

    boolean existsById(Long customerId);
}
