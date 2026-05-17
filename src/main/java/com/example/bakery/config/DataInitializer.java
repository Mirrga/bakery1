package com.example.bakery.config;

import com.example.bakery.feature.product.entity.Category;
import com.example.bakery.feature.product.entity.Product;
import com.example.bakery.feature.product.repository.CategoryRepository;
import com.example.bakery.feature.product.repository.ProductRepository;
import com.example.bakery.feature.user.entity.Role;
import com.example.bakery.feature.user.entity.User;
import com.example.bakery.feature.user.entity.UserRole;
import com.example.bakery.feature.user.repository.RoleRepository;
import com.example.bakery.feature.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Инициализация ролей
        if (roleRepository.count() == 0) {
            for (UserRole userRole : UserRole.values()) {
                roleRepository.save(new Role(userRole));
            }
            System.out.println("✅ Роли успешно добавлены в БД!");
        }

        // Создаем тестовых пользователей, если их нет
        if (userRepository.count() == 0) {
            Role customerRole = roleRepository.findByName(UserRole.CUSTOMER).orElseThrow();
            Role adminRole = roleRepository.findByName(UserRole.ADMIN).orElseThrow();
            Role managerRole = roleRepository.findByName(UserRole.MANAGER).orElseThrow();

            // Администратор
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .email("admin@bakery.com")
                    .firstName("Admin")
                    .lastName("Adminov")
                    .roles(Set.of(adminRole))
                    .build();
            userRepository.save(admin);

            // Менеджер
            User manager = User.builder()
                    .username("manager")
                    .password(passwordEncoder.encode("manager123"))
                    .email("manager@bakery.com")
                    .firstName("Manager")
                    .lastName("Managerov")
                    .roles(Set.of(managerRole))
                    .build();
            userRepository.save(manager);

            // Обычный пользователь
            User customer = User.builder()
                    .username("user")
                    .password(passwordEncoder.encode("user123"))
                    .email("user@bakery.com")
                    .firstName("Ivan")
                    .lastName("Ivanov")
                    .bonusPoints(new BigDecimal("500.00"))
                    .roles(Set.of(customerRole))
                    .build();
            userRepository.save(customer);

            System.out.println("✅ Тестовые пользователи успешно добавлены в БД!");
            System.out.println("   Admin: admin/admin123");
            System.out.println("   Manager: manager/manager123");
            System.out.println("   Customer: user/user123");
        }
        
        // Проверяем, пуста ли БД, чтобы не дублировать данные при каждом перезапуске
        if (categoryRepository.count() == 0) {
            
            // 1. Создаем категории
            Category bread = new Category();
            bread.setName("Хлеб");
            categoryRepository.save(bread);

            Category cakes = new Category();
            cakes.setName("Торты");
            categoryRepository.save(cakes);

            // 2. Создаем товары
            Product borodinsky = new Product();
            borodinsky.setName("Бородинский хлеб");
            borodinsky.setDescription("Классический ржаной хлеб с кориандром");
            borodinsky.setPrice(new BigDecimal("85.00"));
            borodinsky.setCategory(bread);
            productRepository.save(borodinsky);

            Product napoleon = new Product();
            napoleon.setName("Торт Наполеон");
            napoleon.setDescription("Слоеный торт с заварным кремом");
            napoleon.setPrice(new BigDecimal("1200.00"));
            napoleon.setCategory(cakes);
            productRepository.save(napoleon);

            System.out.println("✅ Тестовые данные успешно добавлены в БД!");
        }
    }
}