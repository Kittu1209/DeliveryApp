package com.example.fooddeliveryapp_student;

import java.io.Serializable;

public class AddressModel implements Serializable {
    private String id;
    private String studentName;
    private String phoneNumber;
    private String hostel;
    private String room;
    private String userId;

    // Default Constructor (Required for Firestore)
    public AddressModel() {}

    // Parameterized Constructor
    public AddressModel(String id, String studentName, String phoneNumber, String hostel, String room, String userId) {
        this.id = id;
        this.studentName = studentName;
        this.phoneNumber = phoneNumber;
        this.hostel = hostel;
        this.room = room;
        this.userId = userId;
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getHostel() { return hostel; }
    public void setHostel(String hostel) { this.hostel = hostel; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
