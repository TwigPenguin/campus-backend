package com.example.campus_backend.service;

import com.example.campus_backend.common.CampusText;
import com.example.campus_backend.entity.Complaint;
import com.example.campus_backend.entity.Goods;
import com.example.campus_backend.entity.User;
import com.example.campus_backend.repository.ComplaintRepository;
import com.example.campus_backend.repository.GoodsRepository;
import com.example.campus_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Complaint submit(Long goodsId, Long reporterId, String type, String content) {
        if (reporterId == null) {
            throw new IllegalArgumentException("请先登录");
        }
        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在，请重新登录"));
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("请填写投诉或举报内容");
        }
        if (content.length() > 500) {
            throw new IllegalArgumentException("内容不能超过 500 字");
        }
        Complaint complaint = new Complaint();
        complaint.setType(!"投诉".equals(type) ? "举报" : "投诉");
        complaint.setReporterId(reporterId);
        complaint.setReporterName(CampusText.displayName(reporter));
        complaint.setContent(content.trim());
        if (goodsId != null) {
            goodsRepository.findById(goodsId).ifPresent(goods -> {
                complaint.setGoodsId(goods.getId());
                complaint.setGoodsTitle(goods.getTitle());
            });
        }
        complaint.setStatus("待处理");
        return complaintRepository.save(complaint);
    }

    public List<Complaint> listByReporter(Long reporterId) {
        if (reporterId == null) {
            throw new IllegalArgumentException("请先登录");
        }
        return complaintRepository.findByReporterIdOrderByIdDesc(reporterId);
    }

    public List<Complaint> listAll() {
        return complaintRepository.findAllByOrderByIdDesc();
    }

    @Transactional
    public Complaint handle(Long id, String result) {
        if (id == null) {
            throw new IllegalArgumentException("参数不完整");
        }
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("投诉记录不存在"));
        
        // 1. 更新投诉状态为已处理
        complaint.setStatus("已处理");
        complaint.setHandleResult(result == null ? "" : result.trim());
        complaint.setHandleTime(LocalDateTime.now());
        Complaint savedComplaint = complaintRepository.save(complaint);

        // ================= 新增：封禁违规卖家逻辑 =================
        // 如果这条投诉关联了商品，我们就把这个商品的卖家封禁
        if (savedComplaint.getGoodsId() != null) {
            // 找到被举报的商品
            goodsRepository.findById(savedComplaint.getGoodsId()).ifPresent(goods -> {
                if (goods.getSellerId() != null) {
                    // 找到卖家，并将其状态修改为“封禁”
                    userRepository.findById(goods.getSellerId()).ifPresent(seller -> {
                        seller.setStatus("封禁");
                        userRepository.save(seller);
                    });
                }
            });
        }
        // ==========================================================

        return savedComplaint;
    }
}
