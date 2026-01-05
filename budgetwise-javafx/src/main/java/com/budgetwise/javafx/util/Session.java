package com.budgetwise.javafx.util;

public class Session {
    public static Long userId;
    public static String fullName;
    public static String email;

    public static void clear() {
        userId = null;
        fullName = null;
        email = null;
    }
}