package com.example.fooddeliveryapp_student;

public class Category {
    private String id;
    private String name;
    private String image;
    private boolean isSelected;

    // Required empty constructor for Firestore
    public Category() {}

    public Category(String id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.isSelected = false;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getImage() { return image; }
    public boolean isSelected() { return isSelected; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setImage(String image) { this.image = image; }
    public void setSelected(boolean selected) { isSelected = selected; }
}