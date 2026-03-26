package com.bootcamp.ticketing_api.repository;

import com.bootcamp.ticketing_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);

    // Untuk validasi registrasi (Flowchart Image 4)
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Page<User> findAllByRole(User.Role role, Pageable pageable);

    // Untuk fitur potong saldo (Flowchart Image 11)
    @Modifying
    @Query("UPDATE User u SET u.balance = u.balance - :amount WHERE u.username = :username AND u.balance >= :amount")
    int updateBalance(@Param("username") String username, @Param("amount") Double amount);
}