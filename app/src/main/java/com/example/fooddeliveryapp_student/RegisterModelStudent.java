package com.example.fooddeliveryapp_student;

public class RegisterModelStudent {

    private String stuname;
    private String stuid;
    private String stuemail;
    private String stuphno;
    private String userType;

    // Constructor with parameters
    public RegisterModelStudent(String stuname, String stuid, String stuemail, String stuphno, String userType) {
        this.stuname = stuname;
        this.stuid = stuid;
        this.stuemail = stuemail;
        this.stuphno = stuphno;
        this.userType = userType;
    }

    // Default constructor
    public RegisterModelStudent() {}

    // Getter and setter methods for each field
    public String getStuname() {
        return stuname;
    }

    public void setStuname(String stuname) {
        this.stuname = stuname;
    }

    public String getStuid() {
        return stuid;
    }

    public void setStuid(String stuid) {
        this.stuid = stuid;
    }

    public String getStuemail() {
        return stuemail;
    }

    public void setStuemail(String stuemail) {
        this.stuemail = stuemail;
    }

    public String getStuphno() {
        return stuphno;
    }

    public void setStuphno(String stuphno) {
        this.stuphno = stuphno;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
