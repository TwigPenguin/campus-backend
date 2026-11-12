package com.example.campus_backend.controller;

import com.example.campus_backend.common.ParamUtil;
import com.example.campus_backend.common.Result;
import com.example.campus_backend.entity.TradeOrder;
import com.example.campus_backend.service.OrderService;
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
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public Result<TradeOrder> create(@RequestBody Map<String, Object> body) {
        return Result.success(orderService.create(
                ParamUtil.asLong(body.get("goodsId")),
                ParamUtil.asLong(body.get("buyerId"))
        ));
    }

    @PostMapping("/confirm")
    public Result<TradeOrder> confirm(@RequestBody Map<String, Object> body) {
        return Result.success(orderService.confirm(
                ParamUtil.asLong(body.get("id")),
                ParamUtil.asLong(body.get("buyerId"))
        ));
    }

    @PostMapping("/cancel")
    public Result<TradeOrder> cancel(@RequestBody Map<String, Object> body) {
        return Result.success(orderService.cancel(
                ParamUtil.asLong(body.get("id")),
                ParamUtil.asLong(body.get("buyerId"))
        ));
    }

    @GetMapping("/mine")
    public Result<List<TradeOrder>> mine(@RequestParam Long userId,
                                         @RequestParam(defaultValue = "buy") String type) {
        return Result.success(orderService.list(userId, type));
    }
}
