package com.example.fooddeliveryapp_student;

public class Product {
    private String id;
    private String name;
    private String category;
    private double price;
    private String description;
    private String imageUrl;
    private boolean available;
    private String shopId;

    public Product() {
        // Empty constructor for Firestore deserialization
    }

    public Product(String id, String name, String category, double price, String description, String imageUrl, boolean available, String shopId) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.available = available;
        this.shopId = shopId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public boolean isAvailable() {
        return available;
    }

    public String getShopId() {
        return shopId;
    }
}
