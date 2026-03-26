package com.bootcamp.ticketing_api.controller;

import com.bootcamp.ticketing_api.DTO.BookingRequest;
import com.bootcamp.ticketing_api.DTO.BookingResponse;
import com.bootcamp.ticketing_api.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BookingResponse> bookTicket(
            @Valid @RequestBody BookingRequest request,
            Authentication authentication) {

        // Mengambil user ID dari JWT yang sedang login
        String userId = authentication.getName();

        return ResponseEntity.ok(bookingService.buyTicket(userId, request.getEventCategoryId()));
    }
}