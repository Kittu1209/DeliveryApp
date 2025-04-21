package com.example.fooddeliveryapp_student;

public class CategoryProductModel {
    private String name, imageUrl, description;
    private double price;

    public CategoryProductModel() {
        // Required for Firebase
    }

    public CategoryProductModel(String name, String imageUrl, double price) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.description = description;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }
}
