package com.chronos.AuthService.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class VerificationCodeGenerator {

    private final SecureRandom random = new SecureRandom();

    public String generate6DigitCode() {
        int n = 100_000 + random.nextInt(900_000);
        return String.valueOf(n);
    }
}
