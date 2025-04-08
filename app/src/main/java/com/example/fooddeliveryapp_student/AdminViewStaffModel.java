package com.example.fooddeliveryapp_student;

public class AdminViewStaffModel {
    private String name, email, phone, del_man_id, current_duty, driving_license_no, admin_control;

    public AdminViewStaffModel() {}

    public AdminViewStaffModel(String name, String email, String phone, String del_man_id,
                               String current_duty, String driving_license_no, String admin_control) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.del_man_id = del_man_id;
        this.current_duty = current_duty;
        this.driving_license_no = driving_license_no;
        this.admin_control = admin_control;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getDel_man_id() { return del_man_id; }
    public String getCurrent_duty() { return current_duty; }
    public String getDriving_license_no() { return driving_license_no; }
    public String getAdmin_control() { return admin_control; }
}
