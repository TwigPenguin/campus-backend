package com.example.campus_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;

import java.time.LocalDateTime;

@Entity
public class Goods {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private Double price;
    private Double originalPrice;

    @Column(name = "item_condition")
    private String condition;

    private String location;
    private String seller;
    private Long sellerId;
    private String contact;

    @Column(columnDefinition = "TEXT")
    private String image;

    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    /** 待审核、已上架、已驳回、已下架、已售出 */
    private String status;

    private LocalDateTime createTime;

    public Goods() {}

    public Goods(Long id, String title, Double price, Double originalPrice, String condition,
                 String location, String seller, String image, String category, String description, String status) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.originalPrice = originalPrice;
        this.condition = condition;
        this.location = location;
        this.seller = seller;
        this.image = image;
        this.category = category;
        this.description = description;
        this.status = status;
    }

    @PrePersist
    public void prePersist() {
        if (createTime == null) {
            createTime = LocalDateTime.now();
        }
        if (status == null || status.isBlank()) {
            status = "待审核";
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Double getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(Double originalPrice) { this.originalPrice = originalPrice; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getSeller() { return seller; }
    public void setSeller(String seller) { this.seller = seller; }

    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
