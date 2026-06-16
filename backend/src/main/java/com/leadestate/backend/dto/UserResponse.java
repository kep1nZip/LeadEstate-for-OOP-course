package com.leadestate.backend.dto;

public class UserResponse {

    private Integer id;
    private String name;
    private String email;
    private String role;
    private String phone;

    public UserResponse(Integer id, String name, String email, String role, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.phone = phone;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getPhone() {
        return phone;
    }
}