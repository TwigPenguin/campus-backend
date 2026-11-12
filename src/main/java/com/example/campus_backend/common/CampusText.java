package com.example.campus_backend.common;

import com.example.campus_backend.entity.User;

public final class CampusText {
    private CampusText() {}

    public static String location(String code) {
        if (code == null || code.isBlank()) {
            return "校内自提点";
        }
        return switch (code) {
            case "lib" -> "图书馆服务台";
            case "north" -> "北区宿舍管处";
            case "south" -> "南区食堂门口";
            default -> code;
        };
    }

    public static String displayName(User user) {
        if (user.getNickname() != null && !user.getNickname().isBlank()) {
            return user.getNickname();
        }
        return user.getUsername();
    }
}
