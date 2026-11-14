package com.example.campus_backend.service;

import com.example.campus_backend.entity.SysLog;
import com.example.campus_backend.entity.TradeOrder;
import com.example.campus_backend.entity.User;
import com.example.campus_backend.repository.GoodsRepository;
import com.example.campus_backend.repository.OrderRepository;
import com.example.campus_backend.repository.SysLogRepository;
import com.example.campus_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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

    @Autowired
    private SysLogRepository sysLogRepository;

    public Map<String, Object> stats() {
        LocalDateTime start = LocalDate.now(ZoneId.of("Asia/Shanghai")).atStartOfDay();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("userCount", userRepository.count());
        data.put("onSaleCount", goodsRepository.countByStatus("已上架"));
        data.put("pendingCount", goodsRepository.countByStatus("待审核"));
        data.put("dealCount", orderRepository.countByStatus("已完成"));
        data.put("todayDealCount", orderRepository.countByStatusAndFinishTimeGreaterThanEqual("已完成", start));
        data.put("categories", toCategoryList(goodsRepository.countOnSaleByCategory()));
        data.put("trend", trend(start.minusDays(6)));
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

    private List<Map<String, Object>> trend(LocalDateTime from) {
        List<TradeOrder> finished = orderRepository.findByStatusAndFinishTimeGreaterThanEqual("已完成", from);
        Map<LocalDate, Long> byDay = new LinkedHashMap<>();
        for (int i = 0; i < 7; i++) {
            byDay.put(from.toLocalDate().plusDays(i), 0L);
        }
        for (TradeOrder order : finished) {
            LocalDate day = order.getFinishTime().toLocalDate();
            if (byDay.containsKey(day)) {
                byDay.put(day, byDay.get(day) + 1);
            }
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<LocalDate, Long> entry : byDay.entrySet()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("date", entry.getKey().format(formatter));
            item.put("count", entry.getValue());
            result.add(item);
        }
        return result;
    }

    public List<User> listUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User setUserStatus(Long id, String status) {
        if (id == null) {
            throw new IllegalArgumentException("参数不完整");
        }
        if (!"封禁".equals(status) && !"正常".equals(status)) {
            throw new IllegalArgumentException("不支持的状态");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        if ("ADMIN".equals(user.getRole())) {
            throw new IllegalArgumentException("不能封禁管理员");
        }
        user.setStatus(status);
        return userRepository.save(user);
    }

    public List<SysLog> logs() {
        return sysLogRepository.findTop100ByOrderByIdDesc();
    }
}
