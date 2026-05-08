package com.chronos.AuthService.repository;

import com.chronos.AuthService.entity.EmailVerificationToken;
import com.chronos.AuthService.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationTokenRepo extends JpaRepository<EmailVerificationToken, Long> {
    boolean existsByUser(User user);

    void deleteByUser(User user);

    EmailVerificationToken findByToken(String token);
}
