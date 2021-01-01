package com.animsh.trace.model;

public class UserModel {

    private String uName;
    private String uEmail;
    private String uPhone;
    private String ukey;
    private int uTag;

    public UserModel() {
    }

    public UserModel(String uName, String uEmail, String uPhone, String ukey, int uTag) {
        this.uName = uName;
        this.uEmail = uEmail;
        this.uPhone = uPhone;
        this.ukey = ukey;
        this.uTag = uTag;
    }

    public int getuTag() {
        return uTag;
    }

    public void setuTag(int uTag) {
        this.uTag = uTag;
    }

    public String getUkey() {
        return ukey;
    }

    public void setUkey(String ukey) {
        this.ukey = ukey;
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
