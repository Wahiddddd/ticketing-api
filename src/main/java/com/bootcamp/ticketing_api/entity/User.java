package com.bootcamp.ticketing_api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role; // SUPER_ADMIN, ADMIN, USER

    private Double balance = 0.0;

    private Integer failedAttempt = 0;

    private Boolean isLocked = false;

    // Enum internal untuk Role
    public enum Role {
        SUPER_ADMIN, ADMIN, USER
    }
}