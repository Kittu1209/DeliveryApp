package com.example.fooddeliveryapp_student;

public class RegisterModelStudent {
    public RegisterModelStudent(String stuname, String stuid, String stuemail, String stuphno,String userType) {
        this.stuname = stuname;
        this.stuid = stuid;
        this.stuemail = stuemail;
        this.stuphno = stuphno;
        this.userType=userType;
    }

    public RegisterModelStudent() {
    }

    public String getStuname() {
        return stuname;
    }

    public void setStuname(String stuname) {
        this.stuname = stuname;
    }

    private String stuname;

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    private String userType;

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

    private String stuid;
    private String stuemail;
    private String stuphno;

}
