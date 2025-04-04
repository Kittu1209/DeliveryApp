package com.example.fooddeliveryapp_student;

import java.util.ArrayList;
import java.util.Date;

public class OrderModel {
    private String orderId;
    private Date orderDate;
    private double totalAmount;
    private boolean isDelivered;
    private ArrayList<String> itemsList; // List of ordered items

    public OrderModel(String orderId, Date orderDate, double totalAmount, boolean isDelivered, ArrayList<String> itemsList) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.isDelivered = isDelivered;
        this.itemsList = itemsList;
    }

    public String getOrderId() {
        return orderId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public boolean isDelivered() {
        return isDelivered;
    }

    public ArrayList<String> getItemsList() {
        return itemsList;
    }
}
