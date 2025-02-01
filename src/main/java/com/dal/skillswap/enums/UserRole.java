package com.dal.skillswap.enums;

public enum UserRole {

    USER("USER"),
    ADMIN("ADMIN");

    private final String role;

    private UserRole(String role) {
        this.role = role;
    }

    public String toString() {
        return this.role;
    }
}
