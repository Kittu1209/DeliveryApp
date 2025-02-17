package com.example.fooddeliveryapp_student;

public class ProductModel {

        private String productId;
        private String categoryId;
        private String name;
        private double price;
        private String description;
        private String imageUrl;
        private float rating;
        private int stock;

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    private boolean isActive;

        public ProductModel() {}

        public ProductModel(String productId, String categoryId, String name, double price, String description, String imageUrl, float rating, int stock, boolean isActive) {
            this.productId = productId;
            this.categoryId = categoryId;
            this.name = name;
            this.price = price;
            this.description = description;
            this.imageUrl = imageUrl;
            this.rating = rating;
            this.stock = stock;
            this.isActive = isActive;
        }

        // Getters & Setters
    }


