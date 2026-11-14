package com.example.campus_backend.service;

import com.example.campus_backend.common.CampusText;
import com.example.campus_backend.common.PageResult;
import com.example.campus_backend.entity.Goods;
import com.example.campus_backend.entity.User;
import com.example.campus_backend.repository.GoodsRepository;
import com.example.campus_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class GoodsService {

    private static final Set<String> CATEGORIES = Set.of("books", "digital", "life", "beauty", "other");

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageService messageService;

    @Autowired
    private SysLogService sysLogService;

    public PageResult<Goods> search(String keyword, String category, String sort, int page, int size) {
        if (keyword != null) {
            keyword = keyword.trim();
            if (keyword.length() > 50) {
                keyword = keyword.substring(0, 50);
            }
        }
        int pageSize = Math.min(Math.max(size, 1), 40);
        int pageIndex = Math.max(page, 1) - 1;
        Page<Goods> result = goodsRepository.searchOnSale(
                category,
                keyword,
                PageRequest.of(pageIndex, pageSize, buildSort(sort))
        );
        return new PageResult<>(result.getContent(), result.getTotalElements());
    }

    public Goods detail(Long id, Long viewerId, String role) {
        Goods goods = goodsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("商品不存在或已删除"));
        if ("待审核".equals(goods.getStatus()) || "已驳回".equals(goods.getStatus())) {
            boolean owner = Objects.equals(goods.getSellerId(), viewerId);
            boolean admin = "ADMIN".equals(role);
            if (!owner && !admin) {
                throw new IllegalArgumentException("商品正在审核，暂时不能查看");
            }
        }
        return goods;
    }

    public List<Goods> listBySeller(Long sellerId) {
        if (sellerId == null) {
            throw new IllegalArgumentException("请先登录");
        }
        return goodsRepository.findBySellerIdOrderByIdDesc(sellerId);
    }

    public List<Goods> listPending() {
        return goodsRepository.findByStatusOrderByIdDesc("待审核");
    }

    @Transactional
    public Goods publish(Goods goods) {
        if (goods.getSellerId() == null) {
            throw new IllegalArgumentException("请先登录后再发布");
        }
        User seller = userRepository.findById(goods.getSellerId())
                .orElseThrow(() -> new IllegalArgumentException("用户不存在，请重新登录"));
        String title = goods.getTitle() == null ? "" : goods.getTitle().trim();
        if (title.isEmpty() || title.length() > 50) {
            throw new IllegalArgumentException("物品名称需要在 1 到 50 字之间");
        }
        if (goods.getPrice() == null || goods.getPrice() < 0) {
            throw new IllegalArgumentException("请填写正确的价格");
        }
        if (goods.getCategory() == null || !CATEGORIES.contains(goods.getCategory())) {
            throw new IllegalArgumentException("请选择正确的分类");
        }
        if (goods.getCondition() == null || goods.getCondition().isBlank()) {
            throw new IllegalArgumentException("请选择成色");
        }
        if (goods.getLocation() == null || goods.getLocation().isBlank()) {
            throw new IllegalArgumentException("请选择自提点");
        }
        if (goods.getDescription() != null && goods.getDescription().length() > 500) {
            throw new IllegalArgumentException("描述不能超过 500 字");
        }
        if (goods.getContact() != null && goods.getContact().length() > 50) {
            throw new IllegalArgumentException("联系方式不能超过 50 字");
        }
        if (goods.getImage() != null && goods.getImage().length() > 2000) {
            throw new IllegalArgumentException("图片地址过长，请重新上传");
        }

        goods.setId(null);
        goods.setTitle(title);
        goods.setSeller(CampusText.displayName(seller));
        goods.setStatus("待审核");
        goods.setCreateTime(LocalDateTime.now());
        if (goods.getStock() == null || goods.getStock() < 1) {
            goods.setStock(1);
        }
        if (goods.getImage() == null || goods.getImage().isBlank()) {
            goods.setImage(defaultImage(goods.getCategory()));
        }
        return goodsRepository.save(goods);
    }

    @Transactional
    public Goods updateStatus(Long id, String status, String role, Long adminId, String adminName) {
        if (!"ADMIN".equals(role)) {
            throw new IllegalArgumentException("只有管理员可以审核");
        }
        if (!"已上架".equals(status) && !"已驳回".equals(status)) {
            throw new IllegalArgumentException("不支持的审核结果");
        }
        Goods goods = goodsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("商品不存在"));
        if (!"待审核".equals(goods.getStatus())) {
            throw new IllegalArgumentException("该物品不在待审核状态");
        }
        goods.setStatus(status);
        Goods saved = goodsRepository.save(goods);
        if ("已上架".equals(status)) {
            messageService.send(saved.getSellerId(), "你发布的「" + saved.getTitle() + "」已通过审核，现在可以在首页看到。");
            sysLogService.record(adminId, adminName, "审核通过", "物品「" + saved.getTitle() + "」");
        } else {
            messageService.send(saved.getSellerId(), "你发布的「" + saved.getTitle() + "」未通过审核，可在个人中心修改后重新提交。");
            sysLogService.record(adminId, adminName, "审核驳回", "物品「" + saved.getTitle() + "」");
        }
        return saved;
    }

    @Transactional
    public Goods offShelf(Long id, Long sellerId) {
        Goods goods = requireOwner(id, sellerId);
        if (!"已上架".equals(goods.getStatus())) {
            throw new IllegalArgumentException("只有在售物品可以下架");
        }
        goods.setStatus("已下架");
        return goodsRepository.save(goods);
    }

    @Transactional
    public Goods resubmit(Long id, Long sellerId) {
        Goods goods = requireOwner(id, sellerId);
        if (!"已驳回".equals(goods.getStatus()) && !"已下架".equals(goods.getStatus())) {
            throw new IllegalArgumentException("当前状态不能重新提交");
        }
        goods.setStatus("待审核");
        return goodsRepository.save(goods);
    }

    private Goods requireOwner(Long id, Long sellerId) {
        if (id == null || sellerId == null) {
            throw new IllegalArgumentException("参数不完整");
        }
        Goods goods = goodsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("商品不存在"));
        if (!Objects.equals(goods.getSellerId(), sellerId)) {
            throw new IllegalArgumentException("只能操作自己发布的物品");
        }
        return goods;
    }

    private Sort buildSort(String sort) {
        return switch (sort == null ? "" : sort) {
            case "latest" -> Sort.by(Sort.Order.desc("createTime"), Sort.Order.desc("id"));
            case "priceAsc" -> Sort.by(Sort.Order.asc("price"), Sort.Order.desc("id"));
            case "priceDesc" -> Sort.by(Sort.Order.desc("price"), Sort.Order.desc("id"));
            default -> Sort.by(Sort.Order.desc("id"));
        };
    }

    private String defaultImage(String category) {
        return switch (category == null ? "" : category) {
            case "digital" -> "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=800&q=80";
            case "life" -> "https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=800&q=80";
            case "beauty" -> "https://images.unsplash.com/photo-1556228720-195a672e8a03?w=800&q=80";
            case "other" -> "https://images.unsplash.com/photo-1485965120184-e220f721d03e?w=800&q=80";
            default -> "https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=800&q=80";
        };
    }
}
