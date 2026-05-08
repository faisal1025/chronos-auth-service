package com.chronos.AuthService.messaging;

public class NotifyEmailMessage {
    private String email;
    private String code;
    private String type = "EMAIL_VERIFICATION";

    public NotifyEmailMessage() {
    }

    public NotifyEmailMessage(String email, String code) {
        this.email = email;
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

