package com.bootcamp.ticketing_api.controller;

import com.bootcamp.ticketing_api.DTO.AdminCreateRequest;
import com.bootcamp.ticketing_api.DTO.RegisterRequest;
import com.bootcamp.ticketing_api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    // Registrasi User Umum
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.registerUser(request));
    }

    // Pembuatan Admin (Hanya Super Admin yang boleh akses)
    @PostMapping("/admin/create")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> createAdmin(@Valid @RequestBody AdminCreateRequest request) {
        return ResponseEntity.ok(authService.createAdmin(request));
    }

    // Login Endpoint
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody com.bootcamp.ticketing_api.DTO.LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}