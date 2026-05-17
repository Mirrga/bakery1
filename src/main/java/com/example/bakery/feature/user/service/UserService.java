package com.example.bakery.feature.user.service;

import com.example.bakery.feature.user.dto.RegistrationRequest;
import com.example.bakery.feature.user.dto.UserDto;
import com.example.bakery.feature.user.entity.Role;
import com.example.bakery.feature.user.entity.User;
import com.example.bakery.feature.user.entity.UserRole;
import com.example.bakery.feature.user.repository.RoleRepository;
import com.example.bakery.feature.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDto register(RegistrationRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email уже занят");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true); // В продакшене может требовать подтверждения
        
        // Назначаем роль CUSTOMER по умолчанию
        Role customerRole = roleRepository.findByName(UserRole.CUSTOMER)
                .orElseThrow(() -> new RuntimeException("Роль CUSTOMER не найдена"));
        user.setRoles(Set.of(customerRole));

        User savedUser = userRepository.save(user);
        return UserDto.fromEntity(savedUser);
    }

    public UserDto getByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return UserDto.fromEntity(user);
    }
    
    // Пример бизнес-логики с бонусами
    @Transactional
    public void addBonus(Long userId, double amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        user.setBonusBalance(user.getBonusBalance().add(java.math.BigDecimal.valueOf(amount)));
    }
}