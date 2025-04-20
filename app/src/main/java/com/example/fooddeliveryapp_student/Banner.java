package com.example.fooddeliveryapp_student;

import android.graphics.Color;

public class Banner {
    private String id;
    private String text;
    private int textColor;
    private int backgroundColor;

    // Constructor for text-only banners (default colors)
    public Banner(String id, String text) {
        this(id, text, Color.BLACK, Color.WHITE);
    }

    // Full constructor
    public Banner(String id, String text, int textColor, int backgroundColor) {
        this.id = id;
        this.text = text;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
    }

    // Getters
    public String getId() { return id; }
    public String getText() { return text; }
    public int getTextColor() { return textColor; }
    public int getBackgroundColor() { return backgroundColor; }
}