package com.bootcamp.ticketing_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import com.bootcamp.ticketing_api.service.UserService;
import com.bootcamp.ticketing_api.DTO.UserProfileResponse;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(Authentication auth) {
        return ResponseEntity.ok(userService.getProfile(auth.getName()));
    }

    // Opsional: Jika ada fitur Top Up Saldo
    @PostMapping("/topup")
    public ResponseEntity<String> topUp(@AuthenticationPrincipal UserDetails userDetails,
            @RequestBody com.bootcamp.ticketing_api.DTO.TopUpRequest request) {
        String response = userService.topUpBalance(userDetails.getUsername(), request);
        return ResponseEntity.ok(response);
    }
}
