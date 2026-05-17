package com.example.bakery.feature.user.controller;

import com.example.bakery.feature.user.dto.RegistrationRequest;
import com.example.bakery.feature.user.dto.UserDto;
import com.example.bakery.feature.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationRequest", RegistrationRequest.builder().build());
        return "user/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegistrationRequest request,
                          BindingResult bindingResult,
                          Model model) {
        if (bindingResult.hasErrors()) {
            return "user/register";
        }

        try {
            userService.register(request);
            return "redirect:/login?registered";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "user/register";
        }
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        UserDto user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("user", user);
        return "user/profile";
    }

    // REST API endpoints
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        try {
            UserDto user = userService.findById(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/api/username/{username}")
    @ResponseBody
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        try {
            UserDto user = userService.findByUsername(username);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/api/{id}/bonus/add")
    @ResponseBody
    public ResponseEntity<?> addBonus(@PathVariable Long id, @RequestParam java.math.BigDecimal amount) {
        try {
            userService.addBonus(id, amount);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Бонусы начислены"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @PostMapping("/api/{id}/bonus/use")
    @ResponseBody
    public ResponseEntity<?> useBonus(@PathVariable Long id, @RequestParam java.math.BigDecimal amount) {
        try {
            userService.useBonus(id, amount);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Бонусы использованы"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}
