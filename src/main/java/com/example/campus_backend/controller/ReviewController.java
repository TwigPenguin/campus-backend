package com.example.campus_backend.controller;

import com.example.campus_backend.common.ParamUtil;
import com.example.campus_backend.common.Result;
import com.example.campus_backend.entity.Review;
import com.example.campus_backend.service.ReviewService;
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
@RequestMapping("/api/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/submit")
    public Result<Review> submit(@RequestBody Map<String, Object> body) {
        Long rating = ParamUtil.asLong(body.get("rating"));
        return Result.success(reviewService.submit(
                ParamUtil.asLong(body.get("orderId")),
                ParamUtil.asLong(body.get("buyerId")),
                rating == null ? null : rating.intValue(),
                ParamUtil.asText(body.get("content"))
        ));
    }

    @GetMapping("/seller")
    public Result<List<Review>> bySeller(@RequestParam Long sellerId) {
        return Result.success(reviewService.listBySeller(sellerId));
    }

    @GetMapping("/mine")
    public Result<List<Review>> mine(@RequestParam Long buyerId) {
        return Result.success(reviewService.listByBuyer(buyerId));
    }
}
