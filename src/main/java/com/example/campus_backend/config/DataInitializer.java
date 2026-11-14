package com.example.campus_backend.config;

import com.example.campus_backend.common.PasswordUtil;
import com.example.campus_backend.entity.Goods;
import com.example.campus_backend.entity.PickupPoint;
import com.example.campus_backend.entity.User;
import com.example.campus_backend.repository.GoodsRepository;
import com.example.campus_backend.repository.PickupPointRepository;
import com.example.campus_backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private PickupPointRepository pickupPointRepository;

    @Override
    public void run(String... args) {
        seedPickupPoints();
        if (userRepository.count() > 0) {
            return;
        }
        saveUser("admin", "admin123", "管理员", "ADMIN");
        User li = saveUser("2021001", "123456", "李同学", "USER");
        User wang = saveUser("2021002", "123456", "王同学", "USER");
        seedGoods(li, wang);
        log.info("演示账号已就绪：同学 2021001 / 123456 ，管理员 admin / admin123");
    }

    private void seedPickupPoints() {
        if (pickupPointRepository.count() > 0) {
            return;
        }
        addPickupPoint("图书馆服务台", "图书馆一楼总服务台，营业时间内均可自提", "启用");
        addPickupPoint("北区宿舍管处", "北区宿舍楼管办公室门口", "启用");
        addPickupPoint("南区食堂门口", "南区食堂正门左侧自提柜", "启用");
    }

    private void addPickupPoint(String name, String description, String status) {
        PickupPoint point = new PickupPoint();
        point.setName(name);
        point.setDescription(description);
        point.setStatus(status);
        pickupPointRepository.save(point);
    }

    private User saveUser(String username, String password, String nickname, String role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(PasswordUtil.hash(password));
        user.setNickname(nickname);
        user.setRole(role);
        return userRepository.save(user);
    }

    private void seedGoods(User li, User wang) {
        add(li, "高等数学第七版（上下册）", 25, 89, "9成新", "图书馆服务台", "books",
                "书内页干净，没有笔记，考研刚用完，希望能帮到学弟学妹。", "已上架",
                "https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=800&q=80", "微信：li_math", 30);
        add(li, "线性代数同济第六版", 15, 42, "9成新", "南区食堂门口", "books",
                "只在期末复习时用过，有少量铅笔标注，已擦掉大半。", "已上架",
                "https://images.unsplash.com/photo-1495446815901-a7297e633e8d?w=800&q=80", "微信：li_math", 26);
        add(li, "罗技静音鼠标", 45, 129, "9成新", "北区宿舍管处", "digital",
                "宿舍用了一年，按键灵敏，送鼠标垫。", "已上架",
                "https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=800&q=80", "微信：li_math", 20);
        add(li, "机械键盘青轴", 120, 399, "7成新", "图书馆服务台", "digital",
                "键帽有正常打油，功能完好，适合写代码。", "已上架",
                "https://images.unsplash.com/photo-1511467687858-23d96c32e4ae?w=800&q=80", "微信：li_math", 16);
        add(wang, "宿舍收纳箱三件套", 18, 49, "全新", "北区宿舍管处", "life",
                "搬家多买了一套，未拆封。", "已上架",
                "https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=800&q=80", "微信：wang_dorm", 12);
        add(wang, "基础护肤套装", 40, 168, "全新", "南区食堂门口", "beauty",
                "室友送的，肤质不合适，塑封还在。", "已上架",
                "https://images.unsplash.com/photo-1556228720-195a672e8a03?w=800&q=80", "微信：wang_dorm", 8);
        add(wang, "山地自行车", 260, 899, "7成新", "北区宿舍管处", "other",
                "骑去教学楼很方便，刹车刚换过，可约时间试骑。", "已上架",
                "https://images.unsplash.com/photo-1485965120184-e220f721d03e?w=800&q=80", "微信：wang_dorm", 5);
        add(li, "考研英语真题详解", 18, 68, "9成新", "图书馆服务台", "books",
                "2019-2024 真题，答案页有少量圈画。", "已上架",
                "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=800&q=80", "微信：li_math", 2);
        add(wang, "护眼台灯", 35, 99, "全新", "南区食堂门口", "life",
                "亮度三档，还在保修期内，毕业带不走。", "待审核",
                "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=800&q=80", "微信：wang_dorm", 1);
    }

    private void add(User seller, String title, double price, double original, String condition,
                     String location, String category, String description, String status,
                     String image, String contact, long minusHours) {
        Goods goods = new Goods();
        goods.setTitle(title);
        goods.setPrice(price);
        goods.setOriginalPrice(original);
        goods.setCondition(condition);
        goods.setLocation(location);
        goods.setCategory(category);
        goods.setDescription(description);
        goods.setStatus(status);
        goods.setImage(image);
        goods.setContact(contact);
        goods.setSellerId(seller.getId());
        goods.setSeller(seller.getNickname());
        goods.setCreateTime(LocalDateTime.now().minusHours(minusHours));
        goodsRepository.save(goods);
    }
}
