package com.example.campus_backend.controller;

import com.example.campus_backend.common.ParamUtil;
import com.example.campus_backend.common.Result;
import com.example.campus_backend.entity.PickupPoint;
import com.example.campus_backend.service.PickupPointService;
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
@RequestMapping("/api/pickup-point")
public class PickupPointController {

    @Autowired
    private PickupPointService pickupPointService;

    @Autowired
    private SysLogService sysLogService;

    @GetMapping("/list")
    public Result<List<PickupPoint>> listEnabled() {
        return Result.success(pickupPointService.listEnabled());
    }

    @GetMapping("/all")
    public Result<List<PickupPoint>> listAll(@RequestParam(required = false) String role) {
        if (!"ADMIN".equals(role)) {
            throw new IllegalArgumentException("只有管理员可以管理自提点");
        }
        return Result.success(pickupPointService.listAll());
    }

    @PostMapping("/save")
    public Result<PickupPoint> save(@RequestBody Map<String, Object> body) {
        if (!"ADMIN".equals(ParamUtil.asText(body.get("role")))) {
            throw new IllegalArgumentException("只有管理员可以管理自提点");
        }
        PickupPoint saved = pickupPointService.save(
                ParamUtil.asLong(body.get("id")),
                ParamUtil.asText(body.get("name")),
                ParamUtil.asText(body.get("description")),
                ParamUtil.asText(body.get("status"))
        );
        sysLogService.record(
                ParamUtil.asLong(body.get("adminId")),
                ParamUtil.asText(body.get("adminName")),
                "自提点管理",
                "保存自提点：" + saved.getName()
        );
        return Result.success(saved);
    }

    @PostMapping("/delete")
    public Result<Void> delete(@RequestBody Map<String, Object> body) {
        if (!"ADMIN".equals(ParamUtil.asText(body.get("role")))) {
            throw new IllegalArgumentException("只有管理员可以管理自提点");
        }
        Long id = ParamUtil.asLong(body.get("id"));
        pickupPointService.delete(id);
        sysLogService.record(
                ParamUtil.asLong(body.get("adminId")),
                ParamUtil.asText(body.get("adminName")),
                "自提点管理",
                "删除自提点 ID：" + id
        );
        return Result.success(null);
    }
}
