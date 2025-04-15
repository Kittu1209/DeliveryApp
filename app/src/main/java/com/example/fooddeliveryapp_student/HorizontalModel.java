package com.example.fooddeliveryapp_student;

public class HorizontalModel {
    private String name;
    private String imageBase64;
    private String description;

    public HorizontalModel(String name, String imageBase64, String description) {
        this.name = name;
        this.imageBase64 = imageBase64;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public String getDescription() {
        return description;
    }
}
