package com.example.fooddeliveryapp_student;

import android.os.Parcelable;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.List;

public class order_model_vendor implements Serializable {
    private String orderId, userId, name, phone, hostel, room, status;
    private double totalAmount;
    private Timestamp createdAt;
    private List<ItemModel> items;

    public order_model_vendor() {}

    public order_model_vendor(String orderId, String userId, String name, String phone,
                              String hostel, String room, String status, double totalAmount,
                              Timestamp createdAt, List<ItemModel> items) {
        this.orderId = orderId;
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.hostel = hostel;
        this.room = room;
        this.status = status;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.items = items;
    }

    public String getOrderId() { return orderId; }
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getHostel() { return hostel; }
    public String getRoom() { return room; }
    public String getStatus() { return status; }
    public double getTotalAmount() { return totalAmount; }
    public Timestamp getCreatedAt() { return createdAt; }
    public List<ItemModel> getItems() { return items; }
}
