package com.trading212.project1.core.models;

public enum Role {
    USER("USER"), ADMIN("ADMIN");

    public final String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }

    static Role fromName(String roleName) {
        return switch (roleName.toUpperCase()) {
            case "USER" -> USER;
            case "ADMIN" -> ADMIN;
            default -> throw new RuntimeException("Invalid role name");
        };
    }
}