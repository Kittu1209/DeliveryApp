package com.example.fooddeliveryapp_student;

public class Category {
    private String id;
    private String name;
    private String imageUrl;  // or iconUrl if you prefer
    private boolean isSelected;

    // Constructor with all fields
    public Category(String id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.isSelected = false;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {  // or getIconUrl() if you prefer
        return imageUrl;
    }

    public boolean isSelected() {
        return isSelected;
    }

    // Setters
    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}