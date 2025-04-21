package com.example.fooddeliveryapp_student;
import java.util.Date;
public class ReviewModel {
    private String studentId;
    private String studentName;
    private String shopId;
    private String orderId;
    private int rating;
    private String comment;
    private Date timestamp;

    // Constructor for manual creation
    public ReviewModel(String studentId, String studentName, String shopId,
                       String orderId, int rating, String comment, Date timestamp) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.shopId = shopId;
        this.orderId = orderId;
        this.rating = rating;
        this.comment = comment;
        this.timestamp = timestamp;
    }

    // Empty constructor for Firestore
    public ReviewModel() {}

    // Getters
    public String getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String getShopId() { return shopId; }
    public String getOrderId() { return orderId; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public Date getTimestamp() { return timestamp; }
}