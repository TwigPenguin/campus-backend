package com.example.campus_backend.repository;

import com.example.campus_backend.entity.UserMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<UserMessage, Long> {
    List<UserMessage> findByUserIdOrderByIdDesc(Long userId);
    List<UserMessage> findByUserIdAndReadFlag(Long userId, Boolean readFlag);
    long countByUserIdAndReadFlag(Long userId, Boolean readFlag);
}
