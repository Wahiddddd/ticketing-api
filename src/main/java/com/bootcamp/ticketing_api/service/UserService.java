package com.bootcamp.ticketing_api.service;

import com.bootcamp.ticketing_api.DTO.UserProfileResponse;
import com.bootcamp.ticketing_api.entity.User;
import com.bootcamp.ticketing_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.bootcamp.ticketing_api.DTO.AdminUpdateRequest;
import com.bootcamp.ticketing_api.DTO.TopUpRequest;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserProfileResponse getProfile(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        return UserProfileResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .balance(user.getBalance())
                .build();
    }

    @Transactional
    public String topUpBalance(String username, TopUpRequest request) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
        user.setBalance(user.getBalance() + request.getAmount());
        userRepository.save(user);
        return "Top up berhasil, saldo saat ini: " + user.getBalance();
    }

    public Page<UserProfileResponse> getAllAdmins(Pageable pageable) {
        return userRepository.findAllByRole(User.Role.ADMIN, pageable)
                .map(user -> UserProfileResponse.builder()
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .role(user.getRole().name())
                        .balance(user.getBalance())
                        .build());
    }

    @Transactional
    public String updateAdmin(String adminId, AdminUpdateRequest request) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin tidak ditemukan"));

        if (!admin.getUsername().equals(request.getUsername())
                && userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username sudah digunakan");
        }
        if (!admin.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email sudah digunakan");
        }

        admin.setUsername(request.getUsername());
        admin.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            admin.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        userRepository.save(admin);
        return "akun berhasil di update";
    }

    @Transactional
    public String deleteAdmin(String adminId) {
        if (!userRepository.existsById(adminId)) {
            throw new RuntimeException("Admin tidak ditemukan");
        }
        userRepository.deleteById(adminId);
        return "akun berhasil dihapus / dinonaktifkan";
    }
}
