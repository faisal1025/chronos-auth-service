package com.chronos.AuthService.service;

import com.chronos.AuthService.messaging.NotifyEmailMessage;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class NotifyEmailPublisherTest {

    @Test
    void publishVerificationCode_sendsMessageToKafkaTemplate() {
        KafkaTemplate<String, Object> kafkaTemplate = mock(KafkaTemplate.class);
        NotifyEmailPublisher publisher = new NotifyEmailPublisher(kafkaTemplate);

        String email = "user@example.com";
        String code = "123456";

        publisher.publishVerificationCode(email, code);

        ArgumentCaptor<NotifyEmailMessage> messageCaptor = ArgumentCaptor.forClass(NotifyEmailMessage.class);
        verify(kafkaTemplate, times(1)).send(eq(NotifyEmailPublisher.TOPIC), eq(email), messageCaptor.capture());

        NotifyEmailMessage msg = messageCaptor.getValue();
        assertEquals(email, msg.getEmail());
        assertEquals(code, msg.getCode());
        assertEquals("EMAIL_VERIFICATION", msg.getType());

        verifyNoMoreInteractions(kafkaTemplate);
    }
}

