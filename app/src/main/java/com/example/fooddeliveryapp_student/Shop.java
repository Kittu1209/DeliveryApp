package com.example.fooddeliveryapp_student;

import com.google.firebase.Timestamp;

public class Shop {
    private String id;
    private String name;
    private String category;
    private String description;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    private String image;
    private double averagePrice;
    private double rating;
    private Timestamp createdAt;

    // New fields from your Firestore document
    private String address;
    private String cuisine;
    private String deliveryTime;
    private String email;
    private boolean isActive;
    private String ownerId;
    private String phone;
    private int priceForTwo;
    private boolean promoted;
    private Timestamp updatedAt;
    private String vendorName;

    // Empty constructor needed for Firestore
    public Shop() {}

    // Existing getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }

    public double getAveragePrice() { return averagePrice; }
    public double getRating() { return rating; }
    public Timestamp getCreatedAt() { return createdAt; }

    // Existing setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setDescription(String description) { this.description = description; }
  //  public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setAveragePrice(double averagePrice) { this.averagePrice = averagePrice; }
    public void setRating(double rating) { this.rating = rating; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    // New getters for additional fields
    public String getAddress() { return address; }
    public String getCuisine() { return cuisine; }
    public String getDeliveryTime() { return deliveryTime; }
    public String getEmail() { return email; }
    public boolean isActive() { return isActive; }
    public String getOwnerId() { return ownerId; }
    public String getPhone() { return phone; }
    public int getPriceForTwo() { return priceForTwo; }
    public boolean isPromoted() { return promoted; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public String getVendorName() { return vendorName; }

    // New setters for additional fields
    public void setAddress(String address) { this.address = address; }
    public void setCuisine(String cuisine) { this.cuisine = cuisine; }
    public void setDeliveryTime(String deliveryTime) { this.deliveryTime = deliveryTime; }
    public void setEmail(String email) { this.email = email; }
    public void setActive(boolean active) { isActive = active; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setPriceForTwo(int priceForTwo) { this.priceForTwo = priceForTwo; }
    public void setPromoted(boolean promoted) { this.promoted = promoted; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }
}