package com.example.fooddeliveryapp_student;

public class DeliveryOrderModel {
    private String orderId;
    private String deliveryAddress;
    private String deliveryStatus;  // E.g., "Assigned", "In Progress", "Delivered"

    // Constructor
    public DeliveryOrderModel(String orderId, String deliveryAddress, String deliveryStatus) {
        this.orderId = orderId;
        this.deliveryAddress = deliveryAddress;
        this.deliveryStatus = deliveryStatus;
    }

    // Getters
    public String getOrderId() {
        return orderId;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }
}
