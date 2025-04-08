package com.example.fooddeliveryapp_student;

import com.google.firebase.Timestamp;

import java.util.Date;

public class DeliveryMan {
    private String name;
    private String phone;
    private String del_man_id;
    private String email;
    private String driving_license_no;
    private String current_duty;
    private String admin_control;
    private Timestamp created_at;
    private Timestamp updated_at;

    public DeliveryMan() {
        // Default constructor required for Firebase
    }

    public DeliveryMan(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.driving_license_no = "";
        this.current_duty = "Not Available";
        this.admin_control = "Active";
        this.created_at = new Timestamp(new Date());
        this.updated_at = new Timestamp(new Date());
    }

    // Getters and setters here
}
