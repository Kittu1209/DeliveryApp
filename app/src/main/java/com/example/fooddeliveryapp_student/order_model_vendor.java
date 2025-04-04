package com.example.fooddeliveryapp_student;

import com.google.firebase.Timestamp;
import java.util.List;

public class order_model_vendor {
    private String orderId, name, phone, hostel, room, status, userId;
    private double totalAmount;
    private Timestamp createdAt;
    private List<Object> items;

    public order_model_vendor() {}

    public order_model_vendor(String orderId, String name, String phone, String hostel, String room,
                              String status, double totalAmount, Timestamp createdAt, String userId,
                              List<Object> items) {
        this.orderId = orderId;
        this.name = name;
        this.phone = phone;
        this.hostel = hostel;
        this.room = room;
        this.status = status;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.userId = userId;
        this.items = items;
    }

    public String getOrderId() { return orderId; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getHostel() { return hostel; }
    public String getRoom() { return room; }
    public String getStatus() { return status; }
    public double getTotalAmount() { return totalAmount; }
    public Timestamp getCreatedAt() { return createdAt; }
    public String getUserId() { return userId; }
    public List<Object> getItems() { return items; }
}
