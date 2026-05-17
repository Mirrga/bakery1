package com.example.bakery.feature.product.controller;

import com.example.bakery.feature.product.dto.ProductDto;
import com.example.bakery.feature.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 1. MVC Endpoint: Страница каталога (Thymeleaf)
    @GetMapping
    public String listProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        Page<ProductDto> productsPage = productService.getProductsPage(page, size, "name");
        model.addAttribute("products", productsPage);
        return "products/list"; // Имя шаблона Thymeleaf
    }

    // 2. REST API Endpoint: Получить список JSON (для AJAX/jQuery)
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<Page<ProductDto>> apiListProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(productService.getProductsPage(page, size, "name"));
    }

    // 3. REST API Endpoint: Создать товар (JSON)
    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<ProductDto> apiCreateProduct(@Valid @RequestBody ProductDto dto) {
        ProductDto created = productService.createProduct(dto);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/catalog")
    public String showCatalog(
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        
        // Получаем страницу товаров (по 9 штук, сортировка по имени)
        Page<ProductDto> productsPage = productService.getProductsPage(page, 9, "name");
        
        model.addAttribute("products", productsPage);
        return "products/catalog"; // Имя файла шаблона без .html
    }
}