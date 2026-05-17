package com.example.bakery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. Отключаем CSRF для REST API endpoints (иначе нужен токен в каждом AJAX запросе)
            .csrf(csrf -> csrf.disable())
            
            // 2. Настраиваем доступ
            .authorizeHttpRequests(auth -> auth
                // Разрешаем ВСЕМ доступ к продуктам и корзине (GET и POST)
                .requestMatchers("/products/**", "/cart/**").permitAll()
                
                // Все остальные запросы (например, админка) требуют входа
                .anyRequest().authenticated()
            )
            
            // Стандартная форма входа (если вдруг понадобится)
            .formLogin(form -> form.permitAll());

        return http.build();
    }
}