package com.example.campus_backend.service;

import com.example.campus_backend.common.CampusText;
import com.example.campus_backend.entity.Review;
import com.example.campus_backend.entity.TradeOrder;
import com.example.campus_backend.entity.User;
import com.example.campus_backend.repository.OrderRepository;
import com.example.campus_backend.repository.ReviewRepository;
import com.example.campus_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Review submit(Long orderId, Long buyerId, Integer rating, String content) {
        if (orderId == null || buyerId == null) {
            throw new IllegalArgumentException("参数不完整");
        }
        TradeOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        if (!"已完成".equals(order.getStatus())) {
            throw new IllegalArgumentException("交易完成后才能评价");
        }
        if (!order.getBuyerId().equals(buyerId)) {
            throw new IllegalArgumentException("只能评价自己的订单");
        }
        if (!reviewRepository.findByOrderId(orderId).isEmpty()) {
            throw new IllegalArgumentException("该订单已评价过");
        }
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("评分需要在 1 到 5 之间");
        }
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        Review review = new Review();
        review.setOrderId(orderId);
        review.setGoodsId(order.getGoodsId());
        review.setGoodsTitle(order.getGoodsTitle());
        review.setSellerId(order.getSellerId());
        review.setSellerName(order.getSellerName());
        review.setBuyerId(buyerId);
        review.setBuyerName(CampusText.displayName(buyer));
        review.setRating(rating);
        review.setContent(content == null ? "" : content.trim());
        return reviewRepository.save(review);
    }

    public List<Review> listBySeller(Long sellerId) {
        if (sellerId == null) {
            throw new IllegalArgumentException("参数不完整");
        }
        return reviewRepository.findBySellerIdOrderByIdDesc(sellerId);
    }

    public List<Review> listByBuyer(Long buyerId) {
        if (buyerId == null) {
            throw new IllegalArgumentException("请先登录");
        }
        return reviewRepository.findByBuyerIdOrderByIdDesc(buyerId);
    }

    public double avgRating(Long sellerId) {
        if (sellerId == null) {
            return 0;
        }
        List<Review> reviews = reviewRepository.findBySellerIdOrderByIdDesc(sellerId);
        if (reviews.isEmpty()) {
            return 0;
        }
        double sum = 0;
        for (Review review : reviews) {
            sum += review.getRating() == null ? 0 : review.getRating();
        }
        return Math.round(sum / reviews.size() * 10) / 10.0;
    }
}
