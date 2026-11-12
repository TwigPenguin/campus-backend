package com.example.campus_backend.common;

public final class ParamUtil {
    private ParamUtil() {}

    public static Long asLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        String text = value.toString().trim();
        if (text.isEmpty()) {
            return null;
        }
        return Long.parseLong(text);
    }

    public static String asText(Object value) {
        return value == null ? "" : value.toString().trim();
    }
}
