package com.example.campus_backend.service;

import com.example.campus_backend.entity.PickupPoint;
import com.example.campus_backend.repository.PickupPointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PickupPointService {

    @Autowired
    private PickupPointRepository pickupPointRepository;

    public List<PickupPoint> listEnabled() {
        return pickupPointRepository.findByStatusOrderByIdAsc("启用");
    }

    public List<PickupPoint> listAll() {
        return pickupPointRepository.findAllByOrderByIdAsc();
    }

    public PickupPoint save(Long id, String name, String description, String status) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("请填写自提点名称");
        }
        if (name.length() > 50) {
            throw new IllegalArgumentException("自提点名称不能超过 50 字");
        }
        PickupPoint point;
        if (id != null) {
            point = pickupPointRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("自提点不存在"));
        } else {
            point = new PickupPoint();
        }
        point.setName(name.trim());
        point.setDescription(description == null ? "" : description.trim());
        point.setStatus("启用".equals(status) ? "启用" : "停用");
        return pickupPointRepository.save(point);
    }

    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("参数不完整");
        }
        pickupPointRepository.deleteById(id);
    }
}
