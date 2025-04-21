package com.example.fooddeliveryapp_student;

import com.google.firebase.Timestamp;

public class FilterProductModel {
    private String name;
    private double price;
    private Timestamp createdAt;
    private String imageUrl;

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public FilterProductModel(String name, double price, Timestamp createdAt, String imageUrl) {
        this.name = name;
        this.price = price;
        this.createdAt = createdAt;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
