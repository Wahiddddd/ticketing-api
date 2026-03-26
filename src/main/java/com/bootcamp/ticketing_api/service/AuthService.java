package com.bootcamp.ticketing_api.service;

import com.bootcamp.ticketing_api.DTO.AdminCreateRequest;
import com.bootcamp.ticketing_api.DTO.RegisterRequest;
import com.bootcamp.ticketing_api.entity.User;
import com.bootcamp.ticketing_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import com.bootcamp.ticketing_api.DTO.LoginRequest;
import com.bootcamp.ticketing_api.security.JwtService;
import com.bootcamp.ticketing_api.security.CustomUserDetailsService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    // Registrasi User Umum
    @Transactional
    public String registerUser(RegisterRequest request) {
        validateUniqueUser(request.getUsername(), request.getEmail());

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.USER);
        user.setBalance(0.0); // Saldo awal 0

        userRepository.save(user);
        return "User berhasil didaftarkan";
    }

    // Pembuatan Admin oleh Super Admin
    @Transactional
    public String createAdmin(AdminCreateRequest request) {
        validateUniqueUser(request.getUsername(), request.getEmail());

        User admin = new User();
        admin.setUsername(request.getUsername());
        admin.setEmail(request.getEmail());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setRole(User.Role.ADMIN);

        userRepository.save(admin);
        return "Admin berhasil dibuat oleh Super Admin";
    }

    private void validateUniqueUser(String username, String email) {
        if (userRepository.existsByUsername(username))
            throw new RuntimeException("Username sudah digunakan");
        if (userRepository.existsByEmail(email))
            throw new RuntimeException("Email sudah digunakan");
    }

    @Transactional
    public String login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Username atau password salah"));

        if (Boolean.TRUE.equals(user.getIsLocked())) {
            throw new RuntimeException("Account is locked");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (Exception e) {
            int attempt = user.getFailedAttempt() != null ? user.getFailedAttempt() : 0;
            attempt++;
            user.setFailedAttempt(attempt);
            if (attempt >= 5) {
                user.setIsLocked(true);
            }
            userRepository.save(user);
            throw new RuntimeException("Username atau password salah");
        }

        user.setFailedAttempt(0);
        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        return jwtService.generateToken(userDetails);
    }
}