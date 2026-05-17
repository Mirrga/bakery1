package com.example.bakery.config;

import com.example.bakery.feature.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService userService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. Отключаем CSRF для REST API endpoints (иначе нужен токен в каждом AJAX запросе)
            .csrf(csrf -> csrf.disable())
            
            // 2. Настраиваем доступ
            .authorizeHttpRequests(auth -> auth
                // Разрешаем ВСЕМ доступ к продуктам и корзине (GET и POST)
                .requestMatchers("/products/**", "/cart/**").permitAll()
                // Разрешаем регистрацию и логин
                .requestMatchers("/users/register", "/login", "/error").permitAll()
                // Профиль требует аутентификации
                .requestMatchers("/users/profile", "/users/api/**").authenticated()
                // Все остальные запросы требуют входа
                .anyRequest().authenticated()
            )
            
            // Стандартная форма входа
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
                .defaultSuccessUrl("/products", true)
                .failureUrl("/login?error")
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/products?logout")
                .permitAll()
            )
            .authenticationProvider(authenticationProvider());

        return http.build();
    }
}