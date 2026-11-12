package com.example.campus_backend.service;

import com.example.campus_backend.entity.UserMessage;
import com.example.campus_backend.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public void send(Long userId, String content) {
        if (userId == null || content == null || content.isBlank()) {
            return;
        }
        UserMessage message = new UserMessage();
        message.setUserId(userId);
        message.setContent(content);
        message.setReadFlag(false);
        messageRepository.save(message);
    }

    public List<UserMessage> list(Long userId) {
        requireUser(userId);
        return messageRepository.findByUserIdOrderByIdDesc(userId);
    }

    public long unreadCount(Long userId) {
        requireUser(userId);
        return messageRepository.countByUserIdAndReadFlag(userId, false);
    }

    @Transactional
    public void readAll(Long userId) {
        requireUser(userId);
        List<UserMessage> unread = messageRepository.findByUserIdAndReadFlag(userId, false);
        for (UserMessage message : unread) {
            message.setReadFlag(true);
        }
        messageRepository.saveAll(unread);
    }

    private void requireUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("请先登录");
        }
    }
}
