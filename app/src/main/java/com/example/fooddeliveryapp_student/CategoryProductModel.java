package com.example.fooddeliveryapp_student;
public class CategoryProductModel {
    private String productId;
    private String name;
    private double price;
    private String productImage;

    // Constructor
    public CategoryProductModel(String productId, String name, double price, String productImage) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.productImage = productImage;
    }

    // Getter methods
    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getProductImage() {
        return productImage;
    }
}
