package com.example.workout.Utils;

public class ValidationUtil {

    private static final String VALID_USERNAME_PATTERN = "^[A-Za-z][A-Za-z0-9]{2,}$";
    private static final String INVALID_CHAR_PATTERN = ".*[&=_',<>\"!-].*";

    public static String validateUsername(String username) {
        if (username.isEmpty()) return "Username must not be empty";
        if (username.matches(INVALID_CHAR_PATTERN)) return "Username contains invalid characters";
        if (username.length() < 3 || username.length() > 20) return "Username must be between 3 and 20 characters";
        if (username.matches("\\d+")) return "Username must not be only digits";
        if (!username.matches(VALID_USERNAME_PATTERN)) return "Username must start with a letter and contain only letters and digits";
        return null;
    }

    public static String validatePassword(String password) {
        if (password.isEmpty()) return "Password must not be empty";
        if (password.matches(INVALID_CHAR_PATTERN)) return "Password contains invalid characters";
        if (password.length() < 8) return "Password must be at least 8 characters";
        return null;
    }
}