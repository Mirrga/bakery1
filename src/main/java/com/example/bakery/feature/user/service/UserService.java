package com.example.bakery.feature.user.service;

import com.example.bakery.feature.cart.entity.Cart;
import com.example.bakery.feature.cart.repository.CartRepository;
import com.example.bakery.feature.user.dto.RegistrationRequest;
import com.example.bakery.feature.user.dto.UserDto;
import com.example.bakery.feature.user.entity.Role;
import com.example.bakery.feature.user.entity.User;
import com.example.bakery.feature.user.entity.UserRole;
import com.example.bakery.feature.user.repository.RoleRepository;
import com.example.bakery.feature.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Загрузка пользователя по имени: {}", username);
        return userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));
    }

    @Transactional(readOnly = true)
    public UserDto findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));
        return UserDto.fromEntity(user);
    }

    @Transactional(readOnly = true)
    public UserDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден с ID: " + id));
        return UserDto.fromEntity(user);
    }

    @Transactional
    public UserDto register(RegistrationRequest request) {
        log.info("Регистрация нового пользователя: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Пользователь с таким именем уже существует");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email уже зарегистрирован");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Пароли не совпадают");
        }

        Role customerRole = roleRepository.findByName(UserRole.CUSTOMER)
                .orElseThrow(() -> new IllegalStateException("Роль CUSTOMER не найдена"));

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .roles(Set.of(customerRole))
                .build();

        User savedUser = userRepository.save(user);

        // Создаем корзину для нового пользователя
        Cart cart = Cart.builder()
                .user(savedUser)
                .build();
        cartRepository.save(cart);

        log.info("Пользователь успешно зарегистрирован: {}", savedUser.getUsername());
        return UserDto.fromEntity(savedUser);
    }

    @Transactional
    public void addBonus(Long userId, java.math.BigDecimal amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден с ID: " + userId));
        user.addBonus(amount);
        userRepository.save(user);
        log.info("Начислено {} бонусов пользователю {}", amount, user.getUsername());
    }

    @Transactional
    public void useBonus(Long userId, java.math.BigDecimal amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден с ID: " + userId));
        user.useBonus(amount);
        userRepository.save(user);
        log.info("Списано {} бонусов у пользователя {}", amount, user.getUsername());
    }
}
