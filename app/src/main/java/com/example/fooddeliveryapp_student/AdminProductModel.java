package com.example.fooddeliveryapp_student;

public class AdminProductModel {
    private String productId, name, description, category, imageUrl, shopId;
    private double price;

    public AdminProductModel() {
    }

    public AdminProductModel(String productId, String name, String description, String category, String imageUrl, String shopId, double price) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.category = category;
        this.imageUrl = imageUrl;
        this.shopId = shopId;
        this.price = price;
    }

    public String getProductId() { return productId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getImageUrl() { return imageUrl; }
    public String getShopId() { return shopId; }
    public double getPrice() { return price; }
}
