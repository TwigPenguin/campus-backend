package com.example.campus_backend.repository;

import com.example.campus_backend.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByReporterIdOrderByIdDesc(Long reporterId);
    List<Complaint> findAllByOrderByIdDesc();
}
