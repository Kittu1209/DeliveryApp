package com.example.fooddeliveryapp_student;

import com.google.firebase.Timestamp;
import java.util.Date;
import java.util.Map;

public class AdminApproveVendorModel {
    private String id;
    private String name;
    private String address;
    private String description;
    private String deliveryTime;
    private String priceForTwo;
    private String cuisine;
    private String image;
    private String email;
    private String ownerId;
    private String phone;
    private String vendorName;
    private boolean isActive;
    private boolean promoted;
    private double rating;
    private Date createdAt;
    private Date updatedAt;

    public AdminApproveVendorModel() {
        // Default constructor required for Firestore
    }

    public AdminApproveVendorModel(Map<String, Object> data) {
        try {
            this.id = getStringSafe(data, "id");
            this.name = getStringSafe(data, "name");
            this.address = getStringSafe(data, "address");
            this.description = getStringSafe(data, "description");
            this.deliveryTime = getStringSafe(data, "deliveryTime");
            this.priceForTwo = formatPrice(getNumberSafe(data, "priceForTwo"));
            this.cuisine = getStringSafe(data, "cuisine");
            this.image = getStringSafe(data, "image");
            this.email = getStringSafe(data, "email");
            this.ownerId = getStringSafe(data, "ownerId");
            this.phone = getStringSafe(data, "phone");
            this.vendorName = getStringSafe(data, "vendorName");
            this.isActive = getBooleanSafe(data, "isActive");
            this.promoted = getBooleanSafe(data, "promoted");
            this.rating = getDoubleSafe(data, "rating");
            this.createdAt = getDateSafe(data, "createdAt");
            this.updatedAt = getDateSafe(data, "updatedAt");
        } catch (Exception e) {
            throw new RuntimeException("Error creating vendor model", e);
        }
    }

    // Helper methods for safe data extraction
    private String getStringSafe(Map<String, Object> data, String key) {
        return data.containsKey(key) ? String.valueOf(data.get(key)) : "";
    }

    private boolean getBooleanSafe(Map<String, Object> data, String key) {
        return data.containsKey(key) && Boolean.TRUE.equals(data.get(key));
    }

    private Number getNumberSafe(Map<String, Object> data, String key) {
        return data.containsKey(key) && data.get(key) instanceof Number ?
                (Number) data.get(key) : 0;
    }

    private double getDoubleSafe(Map<String, Object> data, String key) {
        return getNumberSafe(data, key).doubleValue();
    }

    private Date getDateSafe(Map<String, Object> data, String key) {
        if (!data.containsKey(key)) return null;
        Object dateObj = data.get(key);
        if (dateObj instanceof Timestamp) {
            return ((Timestamp) dateObj).toDate();
        } else if (dateObj instanceof Date) {
            return (Date) dateObj;
        }
        return null;
    }

    private String formatPrice(Number price) {
        return "₹" + (price.doubleValue() == price.intValue() ?
                String.valueOf(price.intValue()) : String.valueOf(price.doubleValue()));
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDeliveryTime() { return deliveryTime; }
    public void setDeliveryTime(String deliveryTime) { this.deliveryTime = deliveryTime; }
    public String getPriceForTwo() { return priceForTwo; }
    public void setPriceForTwo(String priceForTwo) { this.priceForTwo = priceForTwo; }
    public String getCuisine() { return cuisine; }
    public void setCuisine(String cuisine) { this.cuisine = cuisine; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public boolean isPromoted() { return promoted; }
    public void setPromoted(boolean promoted) { this.promoted = promoted; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}