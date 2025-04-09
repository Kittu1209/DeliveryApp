package com.example.fooddeliveryapp_student;

public class AssignedDeliveryModel {
    private String orderId;
    private String customerName;
    private String address;
    private String itemName;
    private String deliveryStatus;

    public AssignedDeliveryModel(String orderId, String customerName, String address, String itemName, String deliveryStatus) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.address = address;
        this.itemName = itemName;
        this.deliveryStatus = deliveryStatus;
    }

    // Getters
    public String getOrderId() { return orderId; }
    public String getCustomerName() { return customerName; }
    public String getAddress() { return address; }
    public String getItemName() { return itemName; }
    public String getDeliveryStatus() { return deliveryStatus; }
}
