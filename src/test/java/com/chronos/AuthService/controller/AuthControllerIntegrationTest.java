package com.chronos.AuthService.controller;

import com.chronos.AuthService.config.TestStubsConfig;
import com.chronos.AuthService.repository.EmailVerificationTokenRepo;
import com.chronos.AuthService.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestStubsConfig.class)
class AuthControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailVerificationTokenRepo tokenRepo;

    @BeforeEach
    void setup() {
        tokenRepo.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void register_createsUser_unverified_andReturnsUserResponse() throws Exception {
        String body = "{\"name\":\"Alice\",\"email\":\"alice@example.com\",\"password\":\"Password123!\"}";

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId", notNullValue()))
                .andExpect(jsonPath("$.email", is("alice@example.com")))
                .andExpect(jsonPath("$.name", is("Alice")))
                .andExpect(jsonPath("$.verified", is(false)))
                .andExpect(jsonPath("$.password").doesNotExist());

        // Ensure verification token exists in DB
        var tokens = tokenRepo.findAll();
        org.junit.jupiter.api.Assertions.assertEquals(1, tokens.size());
        org.junit.jupiter.api.Assertions.assertEquals("alice@example.com", tokens.get(0).getEmail());
    }

    @Test
    void login_afterRegister_returnsJwtToken_andUser() throws Exception {
        String registerBody = "{\"name\":\"Bob\",\"email\":\"bob@example.com\",\"password\":\"Password123!\"}";
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated());

        // verify the user first (only verified users can log in)
        String code = tokenRepo.findAll().get(0).getToken();
        mockMvc.perform(get("/api/auth/verify-email/{code}", code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verified", is(true)));

        String loginBody = "{\"email\":\"bob@example.com\",\"password\":\"Password123!\"}";
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token", not(blankOrNullString())))
                .andExpect(jsonPath("$.user.email", is("bob@example.com")))
                .andExpect(jsonPath("$.user.verified", is(true)));
    }

    @Test
    void verifyEmail_withValidCode_marksUserVerified() throws Exception {
        String registerBody = "{\"name\":\"Carol\",\"email\":\"carol@example.com\",\"password\":\"Password123!\"}";
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated());

        String code = tokenRepo.findAll().get(0).getToken();

        mockMvc.perform(get("/api/auth/verify-email/{code}", code))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email", is("carol@example.com")))
                .andExpect(jsonPath("$.verified", is(true)));

        var user = userRepository.findByEmail("carol@example.com").orElseThrow();
        org.junit.jupiter.api.Assertions.assertTrue(user.isVerified());
        org.junit.jupiter.api.Assertions.assertEquals(0, tokenRepo.count(), "token should be deleted after verification");
    }
}
