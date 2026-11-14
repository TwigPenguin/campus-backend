package com.example.campus_backend.repository;

import com.example.campus_backend.entity.PickupPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PickupPointRepository extends JpaRepository<PickupPoint, Long> {
    List<PickupPoint> findByStatusOrderByIdAsc(String status);
    List<PickupPoint> findAllByOrderByIdAsc();
}
