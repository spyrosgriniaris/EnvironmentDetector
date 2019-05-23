package com.myhost.spyros.environmentdetector.Models;

public class User {
    String id;
    String role;
    String email;

    public User(String id, String role, String email) {
        this.id = id;
        this.role = role;
        this.email = email;
    }

    public User() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
