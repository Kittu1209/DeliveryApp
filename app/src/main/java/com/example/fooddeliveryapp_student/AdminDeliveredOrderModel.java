package com.example.fooddeliveryapp_student;

public class AdminDeliveredOrderModel {
    String orderId, userName, itemName, deliveryAddress;
    int quantity;
    double price;

    public AdminDeliveredOrderModel() {}

    public AdminDeliveredOrderModel(String orderId, String userName, String itemName, int quantity, double price, String deliveryAddress) {
        this.orderId = orderId;
        this.userName = userName;
        this.itemName = itemName;
        this.quantity = quantity;
        this.price = price;
        this.deliveryAddress = deliveryAddress;
    }

    // Getters
    public String getOrderId() { return orderId; }
    public String getUserName() { return userName; }
    public String getItemName() { return itemName; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public String getDeliveryAddress() { return deliveryAddress; }
}
