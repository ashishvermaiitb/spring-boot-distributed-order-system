package com.orderfulfillment.orderservice.dto;

import com.orderfulfillment.orderservice.enums.ProductCategory;

import java.math.BigDecimal;

public class OrderItemResponseDto {

    private Long id;
    private String productName;
    private String productDescription;
    private ProductCategory productCategory;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;

    // Constructors
    public OrderItemResponseDto() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductDescription() { return productDescription; }
    public void setProductDescription(String productDescription) { this.productDescription = productDescription; }

    public ProductCategory getProductCategory() { return productCategory; }
    public void setProductCategory(ProductCategory productCategory) { this.productCategory = productCategory; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
}