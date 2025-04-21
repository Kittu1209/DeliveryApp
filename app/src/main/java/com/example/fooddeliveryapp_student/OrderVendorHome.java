package com.example.fooddeliveryapp_student;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class OrderVendorHome {
    private String id;
    private String orderId;
    private String status;
    private double totalAmount;
    private Date createdAt;
    private Map<String, Object> deliveryAddress;
    private List<OrderItem> items; // Changed to List<OrderItem>
    private String assignedDeliveryManId;
    private String userId;

    // Constructor
    public OrderVendorHome() {
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Map<String, Object> getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Map<String, Object> deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public String getAssignedDeliveryManId() {
        return assignedDeliveryManId;
    }

    public void setAssignedDeliveryManId(String assignedDeliveryManId) {
        this.assignedDeliveryManId = assignedDeliveryManId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Helper methods
    public String getCustomerName() {
        if (deliveryAddress != null && deliveryAddress.containsKey("name")) {
            return (String) deliveryAddress.get("name");
        }
        return "Customer";
    }

    public String getCustomerRoom() {
        if (deliveryAddress != null && deliveryAddress.containsKey("room")) {
            return (String) deliveryAddress.get("room");
        }
        return "";
    }

    public String getCustomerHostel() {
        if (deliveryAddress != null && deliveryAddress.containsKey("hostel")) {
            return (String) deliveryAddress.get("hostel");
        }
        return "";
    }
}