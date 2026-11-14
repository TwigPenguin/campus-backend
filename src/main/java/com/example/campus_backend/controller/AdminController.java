package com.example.campus_backend.controller;

import com.example.campus_backend.common.ParamUtil;
import com.example.campus_backend.common.Result;
import com.example.campus_backend.entity.SysLog;
import com.example.campus_backend.entity.User;
import com.example.campus_backend.service.AdminService;
import com.example.campus_backend.service.SysLogService;
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
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private SysLogService sysLogService;

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats(@RequestParam(required = false) String role) {
        if (!"ADMIN".equals(role)) {
            throw new IllegalArgumentException("只有管理员可以查看统计");
        }
        return Result.success(adminService.stats());
    }

    @GetMapping("/users")
    public Result<List<User>> users(@RequestParam(required = false) String role) {
        if (!"ADMIN".equals(role)) {
            throw new IllegalArgumentException("只有管理员可以管理用户");
        }
        return Result.success(adminService.listUsers());
    }

    @PostMapping("/set-user-status")
    public Result<User> setUserStatus(@RequestBody Map<String, Object> body) {
        if (!"ADMIN".equals(ParamUtil.asText(body.get("role")))) {
            throw new IllegalArgumentException("只有管理员可以封禁用户");
        }
        User user = adminService.setUserStatus(
                ParamUtil.asLong(body.get("id")),
                ParamUtil.asText(body.get("status"))
        );
        String action = "封禁".equals(user.getStatus()) ? "封禁用户" : "解封用户";
        sysLogService.record(
                ParamUtil.asLong(body.get("adminId")),
                ParamUtil.asText(body.get("adminName")),
                action,
                "用户：" + user.getUsername()
        );
        return Result.success(user);
    }

    @GetMapping("/log")
    public Result<List<SysLog>> log(@RequestParam(required = false) String role) {
        if (!"ADMIN".equals(role)) {
            throw new IllegalArgumentException("只有管理员可以查看日志");
        }
        return Result.success(adminService.logs());
    }
}
