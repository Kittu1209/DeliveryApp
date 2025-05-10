package com.example.fooddeliveryapp_student;

import java.util.Date;

public class RegisterModelAdmin {
    private String adminID;
    private String name;
    private String email;
    private String phone;
    private String userType;
    private Date createdAt;
    private Date updatedAt;

    public String getAdminID() {
        return adminID;
    }

    public void setAdminID(String adminID) {
        this.adminID = adminID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public RegisterModelAdmin() {}

    public RegisterModelAdmin(String adminID, String name, String email, String phone) {
        this.adminID = adminID;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.userType = "Admin";
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }
}