package com.example.campus_backend.service;

import com.example.campus_backend.entity.SysLog;
import com.example.campus_backend.repository.SysLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysLogService {

    @Autowired
    private SysLogRepository sysLogRepository;

    public void record(Long adminId, String adminName, String action, String detail) {
        SysLog log = new SysLog();
        log.setAdminId(adminId);
        log.setAdminName(adminName == null || adminName.isBlank() ? "管理员" : adminName);
        log.setAction(action);
        log.setDetail(detail);
        sysLogRepository.save(log);
    }

    public List<SysLog> list() {
        return sysLogRepository.findTop100ByOrderByIdDesc();
    }
}
