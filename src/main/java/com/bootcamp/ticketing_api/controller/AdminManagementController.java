package com.bootcamp.ticketing_api.controller;

import com.bootcamp.ticketing_api.DTO.AdminUpdateRequest;
import com.bootcamp.ticketing_api.DTO.UserProfileResponse;
import com.bootcamp.ticketing_api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/superadmin/admins")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class AdminManagementController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<Page<UserProfileResponse>> getAllAdmins(Pageable pageable) {
        return ResponseEntity.ok(userService.getAllAdmins(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateAdmin(@PathVariable String id, @Valid @RequestBody AdminUpdateRequest request) {
        return ResponseEntity.ok(userService.updateAdmin(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAdmin(@PathVariable String id) {
        return ResponseEntity.ok(userService.deleteAdmin(id));
    }
}
