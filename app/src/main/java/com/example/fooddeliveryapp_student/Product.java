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
    private int stockQuantity;  // New field added

    public Product() {
        // Empty constructor for Firestore deserialization
    }

    // Existing constructor (preserved exactly as is)
    public Product(String id, String name, String category, double price, String description,
                   String imageUrl, boolean available, String shopId) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.available = available;
        this.shopId = shopId;
    }

    // New constructor with stockQuantity
    public Product(String id, String name, String category, double price, String description,
                   String imageUrl, boolean available, String shopId, int stockQuantity) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.available = available;
        this.shopId = shopId;
        this.stockQuantity = stockQuantity;
    }

    // Getters and Setters (existing ones preserved, new ones added)
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

    // New getter and setter for stockQuantity
    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}