package com.example.campus_backend.repository;

import com.example.campus_backend.entity.TradeOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<TradeOrder, Long> {
    List<TradeOrder> findByBuyerIdOrderByIdDesc(Long buyerId);
    List<TradeOrder> findBySellerIdOrderByIdDesc(Long sellerId);
    List<TradeOrder> findTop5ByOrderByIdDesc();
    long countByStatusAndFinishTimeGreaterThanEqual(String status, LocalDateTime finishTime);
    long countByStatus(String status);
    List<TradeOrder> findByStatusAndFinishTimeGreaterThanEqual(String status, LocalDateTime finishTime);
}
