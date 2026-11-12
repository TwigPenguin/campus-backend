package com.example.campus_backend.controller;

import com.example.campus_backend.common.Result;
import com.example.campus_backend.entity.User;
import com.example.campus_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        try {
            return Result.success(userService.register(user));
        } catch (Exception e) {
            // 如果没登录成功，把错误信息返回给前端
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/login")
    public Result<User> login(@RequestBody User user) {
        try {
            return Result.success(userService.login(user));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}