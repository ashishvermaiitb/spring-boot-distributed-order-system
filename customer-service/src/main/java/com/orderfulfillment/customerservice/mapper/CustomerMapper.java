package com.orderfulfillment.customerservice.mapper;

import com.orderfulfillment.customerservice.dto.CustomerRequestDto;
import com.orderfulfillment.customerservice.dto.CustomerResponseDto;
import com.orderfulfillment.customerservice.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    /**
     * Convert CustomerRequestDto to Customer Entity
     */
    public Customer toEntity(CustomerRequestDto customerRequestDto) {
        if (customerRequestDto == null) {
            return null;
        }

        Customer customer = new Customer();
        customer.setFirstName(customerRequestDto.getFirstName());
        customer.setLastName(customerRequestDto.getLastName());
        customer.setEmail(customerRequestDto.getEmail());
        customer.setPhoneNumber(customerRequestDto.getPhoneNumber());
        customer.setAddress(customerRequestDto.getAddress());

        return customer;
    }

    /**
     * Convert Customer Entity to CustomerResponseDto
     */
    public CustomerResponseDto toResponseDto(Customer customer) {
        if (customer == null) {
            return null;
        }

        CustomerResponseDto responseDto = new CustomerResponseDto();
        responseDto.setId(customer.getId());
        responseDto.setFirstName(customer.getFirstName());
        responseDto.setLastName(customer.getLastName());
        responseDto.setEmail(customer.getEmail());
        responseDto.setPhoneNumber(customer.getPhoneNumber());
        responseDto.setAddress(customer.getAddress());
        responseDto.setCreatedAt(customer.getCreatedAt());
        responseDto.setUpdatedAt(customer.getUpdatedAt());

        return responseDto;
    }

    /**
     * Update existing Customer Entity from CustomerRequestDto
     */
    public void updateEntityFromDto(CustomerRequestDto customerRequestDto, Customer customer) {
        if (customerRequestDto == null || customer == null) {
            return;
        }

        if (customerRequestDto.getFirstName() != null) {
            customer.setFirstName(customerRequestDto.getFirstName());
        }
        if (customerRequestDto.getLastName() != null) {
            customer.setLastName(customerRequestDto.getLastName());
        }
        if (customerRequestDto.getEmail() != null) {
            customer.setEmail(customerRequestDto.getEmail());
        }
        if (customerRequestDto.getPhoneNumber() != null) {
            customer.setPhoneNumber(customerRequestDto.getPhoneNumber());
        }
        if (customerRequestDto.getAddress() != null) {
            customer.setAddress(customerRequestDto.getAddress());
        }
    }
}