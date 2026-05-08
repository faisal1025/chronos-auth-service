package com.chronos.AuthService.service;

import com.chronos.AuthService.messaging.NotifyEmailMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotifyEmailPublisher {

    public static final String TOPIC = "notify_email";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public NotifyEmailPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishVerificationCode(String email, String code) {
        kafkaTemplate.send(TOPIC, email, new NotifyEmailMessage(email, code));
    }
}

