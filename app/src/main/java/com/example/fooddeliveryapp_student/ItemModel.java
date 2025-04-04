package com.example.fooddeliveryapp_student;

public class ItemModel {
    private String name;
    private double price;
    private int quantity;

    public ItemModel() {}

    public ItemModel(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
}
