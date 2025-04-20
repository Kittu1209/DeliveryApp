package com.example.fooddeliveryapp_student;

public class Filter {
    private String id;
    private String name;
    private boolean isSelected;

    public Filter(String id, String name) {
        this(id, name, false);
    }

    public Filter(String id, String name, boolean isSelected) {
        this.id = id;
        this.name = name;
        this.isSelected = isSelected;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public boolean isSelected() { return isSelected; }
}