package com.example.fooddeliveryapp_student;

public class StudentModel {

    private String stuName;
    private String stuEmail;
    private String stuPhone;
    private String stuId;

    public StudentModel() {
        // Empty constructor for Firestore deserialization
    }

    public StudentModel(String stuName, String stuEmail, String stuPhone, String stuId) {
        this.stuName = stuName;
        this.stuEmail = stuEmail;
        this.stuPhone = stuPhone;
        this.stuId = stuId;
    }

    // Getters and Setters
    public String getStuName() {
        return stuName;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
    }

    public String getStuEmail() {
        return stuEmail;
    }

    public void setStuEmail(String stuEmail) {
        this.stuEmail = stuEmail;
    }

    public String getStuPhone() {
        return stuPhone;
    }

    public void setStuPhone(String stuPhone) {
        this.stuPhone = stuPhone;
    }

    public String getStuId() {
        return stuId;
    }

    public void setStuId(String stuId) {
        this.stuId = stuId;
    }
}
