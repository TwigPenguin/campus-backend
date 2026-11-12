package com.example.campus_backend.controller;

import com.example.campus_backend.common.ParamUtil;
import com.example.campus_backend.common.Result;
import com.example.campus_backend.entity.UserMessage;
import com.example.campus_backend.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/list")
    public Result<List<UserMessage>> list(@RequestParam Long userId) {
        return Result.success(messageService.list(userId));
    }

    @GetMapping("/unread")
    public Result<Long> unread(@RequestParam Long userId) {
        return Result.success(messageService.unreadCount(userId));
    }

    @PostMapping("/read-all")
    public Result<Void> readAll(@RequestBody Map<String, Object> body) {
        messageService.readAll(ParamUtil.asLong(body.get("userId")));
        return Result.success(null);
    }
}
