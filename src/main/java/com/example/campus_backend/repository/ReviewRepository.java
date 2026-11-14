package com.example.campus_backend.repository;

import com.example.campus_backend.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findBySellerIdOrderByIdDesc(Long sellerId);
    List<Review> findByBuyerIdOrderByIdDesc(Long buyerId);
    List<Review> findByOrderId(Long orderId);
    long countBySellerId(Long sellerId);
}
