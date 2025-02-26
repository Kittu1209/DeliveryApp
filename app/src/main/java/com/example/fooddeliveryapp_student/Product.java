package com.example.fooddeliveryapp_student;
public class Product {
    private String id;
    private String name;
    private String category;
    private double price;
    private String description;
    private String imageUrl;

    public Product() {
        // Empty constructor for Firestore
    }

    public Product(String id, String name, String category, double price, String description, String imageUrl) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {  // Firestore will assign an ID
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
