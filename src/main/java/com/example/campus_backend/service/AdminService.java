package com.example.campus_backend.service;

import com.example.campus_backend.repository.GoodsRepository;
import com.example.campus_backend.repository.OrderRepository;
import com.example.campus_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private OrderRepository orderRepository;

    public Map<String, Object> stats() {
        LocalDateTime start = LocalDate.now(ZoneId.of("Asia/Shanghai")).atStartOfDay();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("userCount", userRepository.count());
        data.put("onSaleCount", goodsRepository.countByStatus("已上架"));
        data.put("pendingCount", goodsRepository.countByStatus("待审核"));
        data.put("todayDealCount", orderRepository.countByStatusAndFinishTimeGreaterThanEqual("已完成", start));
        data.put("categories", toCategoryList(goodsRepository.countOnSaleByCategory()));
        data.put("recentOrders", orderRepository.findTop5ByOrderByIdDesc());
        return data;
    }

    private List<Map<String, Object>> toCategoryList(List<Object[]> rows) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("category", row[0] == null ? "other" : row[0].toString());
            item.put("count", row[1] instanceof Number number ? number.longValue() : 0);
            list.add(item);
        }
        return list;
    }
}
