package com.bootcamp.ticketing_api.security;

import com.bootcamp.ticketing_api.entity.User;
import com.bootcamp.ticketing_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Cek apakah sudah ada Super Admin
        if (userRepository.findByUsername("superadmin_aja").isEmpty()) {
            User superAdmin = new User();
            superAdmin.setUsername("superadmin_aja");
            superAdmin.setEmail("superadmin@example.com");
            superAdmin.setPassword(passwordEncoder.encode("password_superadmin"));
            superAdmin.setRole(User.Role.SUPER_ADMIN);
            superAdmin.setBalance(0.0);
            superAdmin.setIsLocked(false);
            superAdmin.setFailedAttempt(0);
            
            userRepository.save(superAdmin);
            System.out.println(">>> Super Admin account created: superadmin_aja / password_superadmin");
        }
    }
}
