package com.itedya.guilds.models;

import java.util.Date;

public class MessageItem {
    private final String message;
    private final long expiresAt;

    public String getMessage() {
        return message;
    }

    public MessageItem(String message, long expiresAt) {
        this.message = message;
        this.expiresAt = expiresAt;
    }

    public boolean isExpired() {
        return new Date().getTime() > expiresAt;
    }
}
