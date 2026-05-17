package com.example.bakery.feature.product.repository;

import com.example.bakery.feature.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // 1. Стандартная пагинация + сортировка
    Page<Product> findAllByOrderByPriceAsc(Pageable pageable);

    // 2. Кастомный JPQL запрос: Поиск товаров дешевле указанной цены
    @Query("SELECT p FROM Product p WHERE p.price <= :maxPrice ORDER BY p.price ASC")
    List<Product> findCheapProducts(@Param("maxPrice") BigDecimal maxPrice);

    // 3. Поиск по категории с пагинацией
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
}