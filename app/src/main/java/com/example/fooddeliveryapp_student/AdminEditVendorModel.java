package com.example.fooddeliveryapp_student;

public class AdminEditVendorModel {
    private String docId;
    private String vendorName, vendorEmail, vendorPhone, vendorId, shopName;

    public AdminEditVendorModel() {}

    public AdminEditVendorModel(String docId, String vendorName, String vendorEmail, String vendorPhone, String vendorId, String shopName) {
        this.docId = docId;
        this.vendorName = vendorName;
        this.vendorEmail = vendorEmail;
        this.vendorPhone = vendorPhone;
        this.vendorId = vendorId;
        this.shopName = shopName;
    }

    public String getDocId() { return docId; }
    public String getVendorName() { return vendorName; }
    public String getVendorEmail() { return vendorEmail; }
    public String getVendorPhone() { return vendorPhone; }
    public String getVendorId() { return vendorId; }
    public String getShopName() { return shopName; }
}
