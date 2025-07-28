package com.orderfulfillment.orderservice.enums;

public enum ProductCategory {
    ELECTRONICS("Electronics and gadgets"),
    CLOTHING("Apparel and accessories"),
    BOOKS("Books and literature"),
    HOME_GARDEN("Home and garden items"),
    SPORTS("Sports and outdoor equipment"),
    TOYS("Toys and games"),
    FOOD("Food and beverages"),
    OTHER("Other categories");

    private final String description;

    ProductCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}