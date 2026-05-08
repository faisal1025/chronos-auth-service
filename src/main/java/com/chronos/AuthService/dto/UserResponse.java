package com.chronos.AuthService.dto;


import com.chronos.AuthService.entity.User;

public class UserResponse {
    private Long userId;
    private String name;
    private String email;
    private String role;
    private boolean verified;

    public UserResponse() {
    }

    public UserResponse(Long userId, String name, String email, String role, boolean verified) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.verified = verified;
    }

    public static UserResponse from(User userEntity) {
        return new UserResponse(userEntity.getId(), userEntity.getName(),
                userEntity.getEmail(), userEntity.getRole().name(), userEntity.isVerified());
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}