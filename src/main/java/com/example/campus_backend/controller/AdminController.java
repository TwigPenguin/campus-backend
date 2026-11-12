package com.example.campus_backend.controller;

import com.example.campus_backend.common.Result;
import com.example.campus_backend.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats(@RequestParam(required = false) String role) {
        if (!"ADMIN".equals(role)) {
            throw new IllegalArgumentException("只有管理员可以查看统计");
        }
        return Result.success(adminService.stats());
    }
}
