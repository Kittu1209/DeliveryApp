package com.example.fooddeliveryapp_student;

import com.google.firebase.Timestamp;

public class Shop {
    private String id;
    private String name;
    private String category;
    private String description;
    private String imageUrl;
    private double averagePrice;
    private double rating;
    private Timestamp createdAt;

    // Empty constructor needed for Firestore
    public Shop() {}

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public double getAveragePrice() { return averagePrice; }
    public double getRating() { return rating; }
    public Timestamp getCreatedAt() { return createdAt; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setDescription(String description) { this.description = description; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setAveragePrice(double averagePrice) { this.averagePrice = averagePrice; }
    public void setRating(double rating) { this.rating = rating; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}