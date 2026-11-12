package com.example.campus_backend.repository;

import com.example.campus_backend.entity.Goods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoodsRepository extends JpaRepository<Goods, Long> {

    List<Goods> findBySellerIdOrderByIdDesc(Long sellerId);

    List<Goods> findByStatusOrderByIdDesc(String status);

    long countByStatus(String status);

    @Query("""
            SELECT g FROM Goods g
            WHERE g.status = '已上架'
              AND (:category IS NULL OR :category = '' OR :category = 'all' OR g.category = :category)
              AND (:keyword IS NULL OR :keyword = ''
                   OR LOWER(g.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(g.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Goods> searchOnSale(@Param("category") String category,
                             @Param("keyword") String keyword,
                             Pageable pageable);

    @Query("SELECT g.category, COUNT(g) FROM Goods g WHERE g.status = '已上架' GROUP BY g.category")
    List<Object[]> countOnSaleByCategory();
}
