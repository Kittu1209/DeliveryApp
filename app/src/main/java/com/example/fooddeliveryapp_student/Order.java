package com.example.fooddeliveryapp_student;
public class Order {
    private String orderId;
    private String status;
    private String assignedDeliveryManId;

    // Constructor
    public Order(String orderId, String status, String assignedDeliveryManId) {
        this.orderId = orderId;
        this.status = status;
        this.assignedDeliveryManId = assignedDeliveryManId;
    }

    // Getter and setter methods
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

    public String getAssignedDeliveryManId() {
        return assignedDeliveryManId;
    }

    public void setAssignedDeliveryManId(String assignedDeliveryManId) {
        this.assignedDeliveryManId = assignedDeliveryManId;
    }
}
