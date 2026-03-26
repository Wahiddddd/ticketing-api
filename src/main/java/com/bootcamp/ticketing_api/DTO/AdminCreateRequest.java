package com.bootcamp.ticketing_api.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminCreateRequest {
    @NotBlank(message = "Username admin wajib diisi")
    private String username;

    @Email(message = "Email tidak valid")
    private String email;

    @NotBlank(message = "Password sementara wajib diisi")
    private String password;

    // Role dikunci ke ADMIN di sisi Service, tidak perlu dikirim dari Frontend
}