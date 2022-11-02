package com.example.reminderapp;

public class User {
    public String id,fullName, email,phone,password;

    public User() {
    }

    public User(String id, String fullName, String email, String phone, String password) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }
}
