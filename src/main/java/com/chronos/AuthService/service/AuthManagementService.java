package com.chronos.AuthService.service;

import com.chronos.AuthService.dto.LoginResponse;
import com.chronos.AuthService.dto.RegisterRequest;
import com.chronos.AuthService.dto.UserResponse;
import com.chronos.AuthService.entity.EmailVerificationToken;
import com.chronos.AuthService.entity.Role;
import com.chronos.AuthService.entity.User;
import com.chronos.AuthService.repository.EmailVerificationTokenRepo;
import com.chronos.AuthService.repository.UserRepository;
import com.chronos.AuthService.utils.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthManagementService {

    private final UserRepository userRepository;
    private final EmailVerificationTokenRepo tokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final VerificationCodeGenerator codeGenerator;
    private final NotifyEmailPublisher notifyEmailPublisher;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthManagementService(
            UserRepository userRepository,
            EmailVerificationTokenRepo tokenRepo,
            PasswordEncoder passwordEncoder,
            VerificationCodeGenerator codeGenerator,
            NotifyEmailPublisher notifyEmailPublisher,
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil
    ) {
        this.userRepository = userRepository;
        this.tokenRepo = tokenRepo;
        this.passwordEncoder = passwordEncoder;
        this.codeGenerator = codeGenerator;
        this.notifyEmailPublisher = notifyEmailPublisher;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public User register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email is already registered");
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(email);
        user.setVerified(false);
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User saved = userRepository.save(user);

        // Create/replace token
        if (tokenRepo.existsByUser(saved)) {
            tokenRepo.deleteByUser(saved);
        }

        String code = codeGenerator.generate6DigitCode();
        EmailVerificationToken token = new EmailVerificationToken();
        token.setEmail(email);
        token.setToken(code);
        token.setUser(saved);
        tokenRepo.save(token);

        // Push to kafka; Notification service will send the email
        notifyEmailPublisher.publishVerificationCode(email, code);

        return saved;
    }

    public LoginResponse login(String email, String password) {
        Authentication auth;
        try {
            auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            email.trim().toLowerCase(),
                            password
                    )
            );
        } catch (BadCredentialsException ex) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        var user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!user.isVerified()) {
            throw new IllegalArgumentException("Email is not verified");
        }

        String token = jwtUtil.generateToken(user);

        return new LoginResponse(token, UserResponse.from(user));
    }

    @Transactional
    public User verifyEmail(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Verification code is required");
        }

        EmailVerificationToken token = tokenRepo.findByToken(code);
        if (token == null) {
            throw new IllegalArgumentException("Invalid verification code");
        }

        if (token.getExpiresAt() != null && token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Verification code has expired");
        }

        User user = token.getUser();
        user.setVerified(true);
        User saved = userRepository.save(user);

        tokenRepo.delete(token);
        return saved;
    }
}
