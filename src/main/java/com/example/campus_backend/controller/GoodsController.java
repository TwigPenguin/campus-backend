package com.example.campus_backend.controller;

import com.example.campus_backend.common.PageResult;
import com.example.campus_backend.common.ParamUtil;
import com.example.campus_backend.common.Result;
import com.example.campus_backend.entity.Goods;
import com.example.campus_backend.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @GetMapping("/list")
    public Result<PageResult<Goods>> getGoodsList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "default") String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int size) {
        return Result.success(goodsService.search(keyword, category, sort, page, size));
    }

    @GetMapping("/{id:\\d+}")
    public Result<Goods> detail(@PathVariable Long id,
                                @RequestParam(required = false) Long viewerId,
                                @RequestParam(required = false) String role) {
        return Result.success(goodsService.detail(id, viewerId, role));
    }

    @GetMapping("/mine")
    public Result<List<Goods>> mine(@RequestParam Long sellerId) {
        return Result.success(goodsService.listBySeller(sellerId));
    }

    @GetMapping("/pending")
    public Result<List<Goods>> pending(@RequestParam(required = false) String role) {
        if (!"ADMIN".equals(role)) {
            throw new IllegalArgumentException("只有管理员可以查看待审核列表");
        }
        return Result.success(goodsService.listPending());
    }

    @PostMapping("/publish")
    public Result<Goods> publishGoods(@RequestBody Goods goods) {
        return Result.success(goodsService.publish(goods));
    }

    @PostMapping("/update-status")
    public Result<Goods> updateStatus(@RequestBody Map<String, Object> body) {
        return Result.success(goodsService.updateStatus(
                ParamUtil.asLong(body.get("id")),
                ParamUtil.asText(body.get("status")),
                ParamUtil.asText(body.get("role")),
                ParamUtil.asLong(body.get("adminId")),
                ParamUtil.asText(body.get("adminName"))
        ));
    }

    @PostMapping("/off-shelf")
    public Result<Goods> offShelf(@RequestBody Map<String, Object> body) {
        return Result.success(goodsService.offShelf(
                ParamUtil.asLong(body.get("id")),
                ParamUtil.asLong(body.get("sellerId"))
        ));
    }

    @PostMapping("/resubmit")
    public Result<Goods> resubmit(@RequestBody Map<String, Object> body) {
        return Result.success(goodsService.resubmit(
                ParamUtil.asLong(body.get("id")),
                ParamUtil.asLong(body.get("sellerId"))
        ));
    }
}
