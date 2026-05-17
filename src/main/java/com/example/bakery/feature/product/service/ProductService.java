package com.example.bakery.feature.product.service;

import com.example.bakery.feature.product.dto.ProductDto;
import com.example.bakery.feature.product.entity.Category;
import com.example.bakery.feature.product.entity.Product;
import com.example.bakery.feature.product.repository.CategoryRepository;
import com.example.bakery.feature.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    // Получение страницы товаров (для MVC View)
    public Page<ProductDto> getProductsPage(int page, int size, String sortBy) {
        Sort sort = Sort.by(Sort.Direction.ASC, sortBy != null ? sortBy : "name");
        Page<Product> products = productRepository.findAll(PageRequest.of(page, size, sort));
        return products.map(this::mapToDto);
    }

    // Получение одного товара
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Товар не найден: " + id));
        return mapToDto(product);
    }

    // Создание товара (Admin only logic will be in Controller/Security)
    @Transactional
    public ProductDto createProduct(ProductDto dto) {
        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new RuntimeException("Категория не найдена: " + dto.categoryId()));

        Product product = Product.builder()
                .name(dto.name())
                .description(dto.description())
                .price(dto.price())
                .imageUrl(dto.imageUrl())
                .category(category)
                .build();

        Product saved = productRepository.save(product);
        return mapToDto(saved);
    }

    // Приватный маппер Entity -> DTO
    private ProductDto mapToDto(Product p) {
        return new ProductDto(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getImageUrl(),
                p.getCategory().getId(),
                p.getCategory().getName()
        );
    }
}