package com.example.campus_backend.service;

import com.example.campus_backend.common.CampusText;
import com.example.campus_backend.entity.Goods;
import com.example.campus_backend.entity.TradeOrder;
import com.example.campus_backend.entity.User;
import com.example.campus_backend.repository.GoodsRepository;
import com.example.campus_backend.repository.OrderRepository;
import com.example.campus_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageService messageService;

    @Transactional
    public TradeOrder create(Long goodsId, Long buyerId) {
        if (goodsId == null || buyerId == null) {
            throw new IllegalArgumentException("参数不完整");
        }
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("请先登录"));
        Goods goods = goodsRepository.findById(goodsId)
                .orElseThrow(() -> new IllegalArgumentException("商品不存在"));
        if (!"已上架".equals(goods.getStatus())) {
            throw new IllegalArgumentException("商品已下架或已售出");
        }
        if (Objects.equals(goods.getSellerId(), buyerId)) {
            throw new IllegalArgumentException("不能购买自己发布的物品");
        }

        goods.setStatus("已售出");
        goodsRepository.save(goods);

        TradeOrder order = new TradeOrder();
        order.setOrderNo(nextOrderNo());
        order.setGoodsId(goods.getId());
        order.setGoodsTitle(goods.getTitle());
        order.setAmount(goods.getPrice());
        order.setBuyerId(buyer.getId());
        order.setBuyerName(CampusText.displayName(buyer));
        order.setSellerId(goods.getSellerId());
        order.setSellerName(goods.getSeller());
        order.setLocation(CampusText.location(goods.getLocation()));
        order.setStatus("待自提");
        TradeOrder saved = orderRepository.save(order);

        String place = CampusText.location(goods.getLocation());
        messageService.send(goods.getSellerId(),
                "「" + goods.getTitle() + "」已被「" + CampusText.displayName(buyer) + "」下单，请到「" + place + "」交接。");
        messageService.send(buyer.getId(),
                "下单成功：「" + goods.getTitle() + "」。请到「" + place + "」与卖家交接，拿到物品后确认收货。");
        return saved;
    }

    @Transactional
    public TradeOrder confirm(Long orderId, Long buyerId) {
        TradeOrder order = requireBuyer(orderId, buyerId);
        if (!"待自提".equals(order.getStatus())) {
            throw new IllegalArgumentException("订单当前状态不能确认收货");
        }
        order.setStatus("已完成");
        order.setFinishTime(LocalDateTime.now());
        TradeOrder saved = orderRepository.save(order);
        messageService.send(order.getSellerId(), "买家已确认收到「" + order.getGoodsTitle() + "」，交易完成。");
        messageService.send(order.getBuyerId(), "你已确认收到「" + order.getGoodsTitle() + "」，交易完成。");
        return saved;
    }

    @Transactional
    public TradeOrder cancel(Long orderId, Long buyerId) {
        TradeOrder order = requireBuyer(orderId, buyerId);
        if (!"待自提".equals(order.getStatus())) {
            throw new IllegalArgumentException("订单当前状态不能取消");
        }
        order.setStatus("已取消");
        TradeOrder saved = orderRepository.save(order);
        goodsRepository.findById(order.getGoodsId()).ifPresent(goods -> {
            if ("已售出".equals(goods.getStatus())) {
                goods.setStatus("已上架");
                goodsRepository.save(goods);
            }
        });
        messageService.send(order.getSellerId(), "买家取消了「" + order.getGoodsTitle() + "」的订单，物品已重新上架。");
        return saved;
    }

    public List<TradeOrder> list(Long userId, String type) {
        if (userId == null) {
            throw new IllegalArgumentException("请先登录");
        }
        if ("sell".equals(type)) {
            return orderRepository.findBySellerIdOrderByIdDesc(userId);
        }
        return orderRepository.findByBuyerIdOrderByIdDesc(userId);
    }

    private TradeOrder requireBuyer(Long orderId, Long buyerId) {
        if (orderId == null || buyerId == null) {
            throw new IllegalArgumentException("参数不完整");
        }
        TradeOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        if (!Objects.equals(order.getBuyerId(), buyerId)) {
            throw new IllegalArgumentException("只能操作自己的订单");
        }
        return order;
    }

    private String nextOrderNo() {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int rand = ThreadLocalRandom.current().nextInt(100, 1000);
        return "ORD" + time + rand;
    }
}
