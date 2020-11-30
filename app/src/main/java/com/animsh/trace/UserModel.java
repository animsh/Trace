package com.animsh.trace;

public class UserModel {

    private String uName;
    private String uEmail;
    private String uPhone;

    public UserModel() {
    }

    public UserModel(String uName, String uEmail, String uPhone) {
        this.uName = uName;
        this.uEmail = uEmail;
        this.uPhone = uPhone;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public String getuPhone() {
        return uPhone;
    }

    public void setuPhone(String uPhone) {
        this.uPhone = uPhone;
    }
}
