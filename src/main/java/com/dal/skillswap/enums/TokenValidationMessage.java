package com.dal.skillswap.enums;

public enum TokenValidationMessage {
    SUCCESS("success"),
    INVALID("invalid"),
    EXPIRED("expired");

    private final String message;

    private TokenValidationMessage(String s) {
        message = s;
    }

    public String toString() {
        return this.message;
    }
}
