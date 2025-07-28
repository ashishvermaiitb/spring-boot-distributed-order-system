package com.orderfulfillment.customerservice.config;

import com.orderfulfillment.customerservice.entity.Customer;
import com.orderfulfillment.customerservice.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("local") // Only runs in local profile
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final CustomerRepository customerRepository;

    @Autowired
    public DataInitializer(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (customerRepository.count() == 0) {
            logger.info("Initializing sample customer data...");

            Customer customer1 = new Customer();
            customer1.setFirstName("John");
            customer1.setLastName("Doe");
            customer1.setEmail("john.doe@example.com");
            customer1.setPhoneNumber("1234567890");
            customer1.setAddress("123 Main Street, New York, NY 10001");

            Customer customer2 = new Customer();
            customer2.setFirstName("Jane");
            customer2.setLastName("Smith");
            customer2.setEmail("jane.smith@example.com");
            customer2.setPhoneNumber("9876543210");
            customer2.setAddress("456 Oak Avenue, Los Angeles, CA 90210");

            Customer customer3 = new Customer();
            customer3.setFirstName("Bob");
            customer3.setLastName("Johnson");
            customer3.setEmail("bob.johnson@example.com");
            customer3.setPhoneNumber("5555551234");
            customer3.setAddress("789 Pine Road, Chicago, IL 60601");

            customerRepository.save(customer1);
            customerRepository.save(customer2);
            customerRepository.save(customer3);

            logger.info("Sample customer data initialized successfully");
        } else {
            logger.info("Customer data already exists, skipping initialization");
        }
    }
}