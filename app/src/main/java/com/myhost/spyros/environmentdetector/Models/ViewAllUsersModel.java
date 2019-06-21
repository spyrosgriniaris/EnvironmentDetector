package com.myhost.spyros.environmentdetector.Models;

public class ViewAllUsersModel {
    String email, id, role;


    public ViewAllUsersModel(){

    }

    public ViewAllUsersModel(String userEmail, String userId, String userRole) {
        this.email = userEmail;
        this.id = userId;
        this.role = userRole;
    }

    public String getUserEmail() {
        return email;
    }

    public String getUserId() {
        return id;
    }

    public String getUserRole() {
        return role;
    }
}
