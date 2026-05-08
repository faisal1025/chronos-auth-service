package com.chronos.AuthService.config;

import com.chronos.AuthService.service.NotifyEmailPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class TestStubsConfig {

    @Bean
    @Primary
    public NotifyEmailPublisher notifyEmailPublisherStub() {
        // Stub to avoid requiring KafkaTemplate in tests.
        return new NotifyEmailPublisher(null) {
            @Override
            public void publishVerificationCode(String email, String code) {
                // no-op
            }
        };
    }
}

