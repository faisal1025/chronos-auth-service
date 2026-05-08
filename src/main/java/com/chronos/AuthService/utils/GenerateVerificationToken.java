package com.chronos.AuthService.utils;

import com.chronos.AuthService.entity.EmailVerificationToken;
import com.chronos.AuthService.entity.User;

import java.util.UUID;

public class GenerateVerificationToken {

    public static EmailVerificationToken generateToken(String email, User user) {
        EmailVerificationToken token = new EmailVerificationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setEmail(email);
        return token;
    }
}
