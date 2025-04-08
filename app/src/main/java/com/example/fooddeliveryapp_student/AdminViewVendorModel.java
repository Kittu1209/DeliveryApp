package com.example.fooddeliveryapp_student;

public class AdminViewVendorModel {
    private String shopName, vendorEmail, vendorId, vendorName, vendorPhone;

    public AdminViewVendorModel() {
    }

    public AdminViewVendorModel(String shopName, String vendorEmail, String vendorId, String vendorName, String vendorPhone) {
        this.shopName = shopName;
        this.vendorEmail = vendorEmail;
        this.vendorId = vendorId;
        this.vendorName = vendorName;
        this.vendorPhone = vendorPhone;
    }

    public String getShopName() { return shopName; }
    public String getVendorEmail() { return vendorEmail; }
    public String getVendorId() { return vendorId; }
    public String getVendorName() { return vendorName; }
    public String getVendorPhone() { return vendorPhone; }

    public void setShopName(String shopName) { this.shopName = shopName; }
    public void setVendorEmail(String vendorEmail) { this.vendorEmail = vendorEmail; }
    public void setVendorId(String vendorId) { this.vendorId = vendorId; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }
    public void setVendorPhone(String vendorPhone) { this.vendorPhone = vendorPhone; }
}
