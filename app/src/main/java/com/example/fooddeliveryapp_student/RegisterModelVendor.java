package com.example.fooddeliveryapp_student;

public class RegisterModelVendor {
    private String vendorName;
    private String vendorId;
    private String vendorEmail;
    private String vendorPhone;
    private String shopName;
    private String userType;

    public RegisterModelVendor(String vendorName, String vendorId, String vendorEmail, String vendorPhone, String shopName, String userType) {
        this.vendorName = vendorName;
        this.vendorId = vendorId;
        this.vendorEmail = vendorEmail;
        this.vendorPhone = vendorPhone;
        this.shopName = shopName;
        this.userType = userType;
    }

    public RegisterModelVendor() {}

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getVendorEmail() {
        return vendorEmail;
    }

    public void setVendorEmail(String vendorEmail) {
        this.vendorEmail = vendorEmail;
    }

    public String getVendorPhone() {
        return vendorPhone;
    }

    public void setVendorPhone(String vendorPhone) {
        this.vendorPhone = vendorPhone;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
