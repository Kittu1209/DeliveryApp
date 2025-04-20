package com.example.fooddeliveryapp_student;

import java.util.List;
import java.util.Map;

public class OrderHistoryModel {

    private String orderId;
    private String status;
    private String name;
    private String phone;
    private String hostel;
    private String room;
    private long totalAmount;
    private List<Map<String, Object>> items;
    private String assignedDeliveryManId;  // New field
    private Map<String, Object> deliveryAddress;  // New field to hold delivery address

    // Constructor to match the data structure
    public OrderHistoryModel(String orderId, String status, String name, String phone,
                             String hostel, String room, long totalAmount,
                             List<Map<String, Object>> items,
                             Map<String, Object> deliveryAddress) {
        this.orderId = orderId;
        this.status = status;
        this.name = name;
        this.phone = phone;
        this.hostel = hostel;
        this.room = room;
        this.totalAmount = totalAmount;
        this.items = items;
        this.assignedDeliveryManId = assignedDeliveryManId;
        this.deliveryAddress = deliveryAddress;
    }

    // Getters for the fields
    public String getOrderId() {
        return orderId;
    }

    public String getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getHostel() {
        return hostel;
    }

    public String getRoom() {
        return room;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public List<Map<String, Object>> getItems() {
        return items;
    }

    public String getAssignedDeliveryManId() {
        return assignedDeliveryManId;
    }

    public Map<String, Object> getDeliveryAddress() {
        return deliveryAddress;
    }
}
