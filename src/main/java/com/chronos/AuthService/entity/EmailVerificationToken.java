package com.chronos.AuthService.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;

import java.time.LocalDateTime;

@Entity
public class EmailVerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Email (message = "Enter a valid email")
    private String email;
    private String token;
    private LocalDateTime expiresAt = LocalDateTime.now().plusHours(24);
    @OneToOne
    private User user;

    public EmailVerificationToken() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String verificationToken) {
        this.token = verificationToken;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
