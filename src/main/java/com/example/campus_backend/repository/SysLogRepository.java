package com.example.campus_backend.repository;

import com.example.campus_backend.entity.SysLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysLogRepository extends JpaRepository<SysLog, Long> {
    List<SysLog> findTop100ByOrderByIdDesc();
}
