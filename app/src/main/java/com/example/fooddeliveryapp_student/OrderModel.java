package com.example.fooddeliveryapp_student;

import java.util.Date;
import java.util.List;

public class OrderModel {
    private String orderId;
    private Date orderDate;
    private double totalAmount;
    private boolean isDelivered;
    private boolean isReviewed;
    private String shopId;
    private List<String> itemsList;

    public OrderModel(String orderId, Date orderDate, double totalAmount, boolean isDelivered,
                      boolean isReviewed, String shopId, List<String> itemsList) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.isDelivered = isDelivered;
        this.isReviewed = isReviewed;
        this.shopId = shopId;
        this.itemsList = itemsList;
    }

    // Getters
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

    public boolean isReviewed() {
        return isReviewed;
    }

    public String getShopId() {
        return shopId;
    }

    public List<String> getItemsList() {
        return itemsList;
    }
}