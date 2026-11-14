package com.example.campus_backend.controller;

import com.example.campus_backend.common.ParamUtil;
import com.example.campus_backend.common.Result;
import com.example.campus_backend.entity.Complaint;
import com.example.campus_backend.service.ComplaintService;
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
@RequestMapping("/api/complaint")
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private SysLogService sysLogService;

    @PostMapping("/submit")
    public Result<Complaint> submit(@RequestBody Map<String, Object> body) {
        return Result.success(complaintService.submit(
                ParamUtil.asLong(body.get("goodsId")),
                ParamUtil.asLong(body.get("reporterId")),
                ParamUtil.asText(body.get("type")),
                ParamUtil.asText(body.get("content"))
        ));
    }

    @GetMapping("/mine")
    public Result<List<Complaint>> mine(@RequestParam Long userId) {
        return Result.success(complaintService.listByReporter(userId));
    }

    @GetMapping("/list")
    public Result<List<Complaint>> list(@RequestParam(required = false) String role) {
        if (!"ADMIN".equals(role)) {
            throw new IllegalArgumentException("只有管理员可以查看投诉");
        }
        return Result.success(complaintService.listAll());
    }

    @PostMapping("/handle")
    public Result<Complaint> handle(@RequestBody Map<String, Object> body) {
        if (!"ADMIN".equals(ParamUtil.asText(body.get("role")))) {
            throw new IllegalArgumentException("只有管理员可以处理投诉");
        }
        Complaint complaint = complaintService.handle(
                ParamUtil.asLong(body.get("id")),
                ParamUtil.asText(body.get("result"))
        );
        sysLogService.record(
                ParamUtil.asLong(body.get("adminId")),
                ParamUtil.asText(body.get("adminName")),
                "处理投诉",
                "处理投诉 ID：" + complaint.getId()
        );
        return Result.success(complaint);
    }
}
