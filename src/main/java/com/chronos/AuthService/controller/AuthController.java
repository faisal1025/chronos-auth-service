package com.chronos.AuthService.controller;

import com.chronos.AuthService.dto.LoginRequest;
import com.chronos.AuthService.dto.LoginResponse;
import com.chronos.AuthService.dto.RegisterRequest;
import com.chronos.AuthService.dto.UserResponse;
import com.chronos.AuthService.entity.User;
import com.chronos.AuthService.repository.UserRepository;
import com.chronos.AuthService.service.AuthManagementService;
import com.chronos.AuthService.utils.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthManagementService authManagementService;


    public AuthController(
            AuthManagementService authManagementService
    ) {
        this.authManagementService = authManagementService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        User created = authManagementService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(created));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authManagementService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify-email/{code}")
    public ResponseEntity<UserResponse> verifyEmail(@PathVariable("code") String code) {
        User verified = authManagementService.verifyEmail(code);
        return ResponseEntity.status(HttpStatus.OK).body(UserResponse.from(verified));
    }
}
