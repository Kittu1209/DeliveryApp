package com.example.fooddeliveryapp_student;

public class AdminOrderModel {
    private String orderId, itemName, studentName, status, imageUrl;
    private int quantity;
    private double price;
    private String deliveryAddress;

    public AdminOrderModel() {}

    public AdminOrderModel(String orderId, String itemName, double price, int quantity, String studentName, String deliveryAddress, String status, String imageUrl) {
        this.orderId = orderId;
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
        this.studentName = studentName;
        this.deliveryAddress = deliveryAddress;
        this.status = status;
        this.imageUrl = imageUrl;
    }

    public String getOrderId() { return orderId; }
    public String getItemName() { return itemName; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public String getStudentName() { return studentName; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public String getStatus() { return status; }
    public String getImageUrl() { return imageUrl; }
}
