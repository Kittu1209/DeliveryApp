package com.example.fooddeliveryapp_student;

public class CategoryModel {

        private String categoryId;
        private String name;
        private String slug;
        private String description;
        private String image;
        private String color;
        private boolean isActive;

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImageUrl(String image) {
        this.image = image;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    // Required Empty Constructor for Firebase
        public CategoryModel() {}

        public CategoryModel(String categoryId, String name, String slug, String description, String image, String color, boolean isActive) {
            this.categoryId = categoryId;
            this.name = name;
            this.slug = slug;
            this.description = description;
            this.image = image;
            this.color = color;
            this.isActive = isActive;
        }

        // Getters & Setters
    }


