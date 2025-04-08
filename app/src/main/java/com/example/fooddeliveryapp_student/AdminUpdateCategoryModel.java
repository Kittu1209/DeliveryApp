package com.example.fooddeliveryapp_student;

public class AdminUpdateCategoryModel {
    private String id;
    private String name;
    private String slug;
    private String description;
    private String color;
    private String image; // base64
    private boolean isActive;

    public AdminUpdateCategoryModel() {
    }

    public AdminUpdateCategoryModel(String id, String name, String slug, String description, String color, String image, boolean isActive) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.color = color;
        this.image = image;
        this.isActive = isActive;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getSlug() { return slug; }
    public String getDescription() { return description; }
    public String getColor() { return color; }
    public String getImage() { return image; }
    public boolean isActive() { return isActive; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setSlug(String slug) { this.slug = slug; }
    public void setDescription(String description) { this.description = description; }
    public void setColor(String color) { this.color = color; }
    public void setImage(String image) { this.image = image; }
    public void setActive(boolean active) { isActive = active; }
}
