package com.example.drawer;

public class Users {

    String fullName, phone, userName;
    public Users() {
    }
    public Users(String fullName, String phone, String userName) {
        this.fullName = fullName;
        this.phone = phone;
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
