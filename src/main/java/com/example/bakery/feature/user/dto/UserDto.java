package com.example.bakery.feature.user.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;
import com.example.bakery.feature.user.entity.UserRole;

@Data
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private BigDecimal bonusBalance;
    private Set<UserRole> roles;

    public static UserDto fromEntity(com.example.bakery.feature.user.entity.User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .bonusBalance(user.getBonusBalance())
                .roles(user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()))
                .build();
    }
}
// Внутренний класс или отдельный файл для маппинга, если Role не импортирован напрямую в DTO
class Role {
    public static UserRole getName(com.example.bakery.feature.user.entity.Role r) {
        return r.getName();
    }
}