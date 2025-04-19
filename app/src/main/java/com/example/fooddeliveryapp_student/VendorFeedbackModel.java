package com.example.fooddeliveryapp_student;

public class VendorFeedbackModel {
    private String name, email, subject, message;

    public VendorFeedbackModel() {} // Required for Firestore

    public VendorFeedbackModel(String name, String email, String subject, String message) {
        this.name = name;
        this.email = email;
        this.subject = subject;
        this.message = message;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getSubject() { return subject; }
    public String getMessage() { return message; }
}
