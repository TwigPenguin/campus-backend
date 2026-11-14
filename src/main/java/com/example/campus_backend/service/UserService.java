package com.example.campus_backend.service;

import com.example.campus_backend.common.CampusText;
import com.example.campus_backend.common.PasswordUtil;
import com.example.campus_backend.entity.User;
import com.example.campus_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User register(User body) {
        String username = body.getUsername() == null ? "" : body.getUsername().trim();
        String password = body.getPassword() == null ? "" : body.getPassword();
        String nickname = body.getNickname() == null ? "" : body.getNickname().trim();
        if (username.isEmpty() || password.isEmpty()) {
            throw new IllegalArgumentException("请填写学号和密码");
        }
        if (username.length() < 2 || username.length() > 32) {
            throw new IllegalArgumentException("学号长度为 2 到 32 位");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("密码至少 6 位");
        }
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("该学号已注册");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(PasswordUtil.hash(password));
        user.setNickname(nickname.isEmpty() ? username : nickname);
        user.setRole("USER");
        return userRepository.save(user);
    }

    public User login(User body) {
        String username = body.getUsername() == null ? "" : body.getUsername().trim();
        String password = body.getPassword() == null ? "" : body.getPassword();
        if (username.isEmpty() || password.isEmpty()) {
            throw new IllegalArgumentException("请填写学号和密码");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("学号或密码错误"));
        if (!PasswordUtil.hash(password).equals(user.getPassword())) {
            throw new IllegalArgumentException("学号或密码错误");
        }
        if ("封禁".equals(user.getStatus())) {
            throw new IllegalArgumentException("该账号已被封禁，请联系管理员");
        }
        return user;
    }
}
