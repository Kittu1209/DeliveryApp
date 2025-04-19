package com.example.fooddeliveryapp_student;

public class StudentFeedbackModel {
    private String name;
    private String email;
    private String subject;
    private String message;
    private long timestamp;

    public StudentFeedbackModel() {
        // Required empty constructor
    }

    public StudentFeedbackModel(String name, String email, String subject, String message, long timestamp) {
        this.name = name;
        this.email = email;
        this.subject = subject;
        this.message = message;
        this.timestamp = timestamp;
    }

    // Getters
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getSubject() { return subject; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }
}
