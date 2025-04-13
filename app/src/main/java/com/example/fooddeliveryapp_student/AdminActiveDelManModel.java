package com.example.fooddeliveryapp_student;

public class AdminActiveDelManModel {
    private String name, email, phone, del_men_address, del_man_id, admin_control, driving_license_no;

    public AdminActiveDelManModel() {}

    public AdminActiveDelManModel(String name, String email, String phone, String del_men_address,
                                  String del_man_id, String admin_control, String driving_license_no) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.del_men_address = del_men_address;
        this.del_man_id = del_man_id;
        this.admin_control = admin_control;
        this.driving_license_no = driving_license_no;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getDel_men_address() { return del_men_address; }
    public String getDel_man_id() { return del_man_id; }
    public String getAdmin_control() { return admin_control; }
    public String getDriving_license_no() { return driving_license_no; }

    public void setAdmin_control(String admin_control) {
        this.admin_control = admin_control;
    }
}
