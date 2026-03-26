package com.bootcamp.ticketing_api.controller;

import com.bootcamp.ticketing_api.DTO.TicketResponse;
import com.bootcamp.ticketing_api.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    // Melihat daftar tiket yang sudah dibeli oleh user yang sedang login
    @GetMapping("/my-tickets")
    public ResponseEntity<Page<TicketResponse>> getMyTickets(Authentication auth, Pageable pageable) {
        String userId = auth.getName();
        return ResponseEntity.ok(ticketService.getUserTickets(userId, pageable));
    }

    // Membatalkan tiket (Flowchart: Refund Saldo)
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<String> cancelTicket(@PathVariable Long id, Authentication auth) {
        String userId = auth.getName();
        ticketService.cancelAndRefundTicket(id, userId);
        return ResponseEntity.ok("Tiket berhasil dibatalkan dan saldo dikembalikan");
    }
}