package com.bootcamp.ticketing_api.controller;

import com.bootcamp.ticketing_api.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/superadmin/tickets")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
public class AdminTicketController {

    private final TicketService ticketService;

    @PatchMapping("/{ticketCode}/disable")
    public ResponseEntity<String> disableTicket(@PathVariable String ticketCode) {
        ticketService.disableTicket(ticketCode);
        return ResponseEntity.ok("Tiket berhasil di-disable / dipindahkan statunya menjadi BROKEN");
    }

    @PatchMapping("/{ticketCode}/move-seat")
    public ResponseEntity<String> moveSeat(@PathVariable String ticketCode, @RequestParam String newSeatNumber) {
        ticketService.moveSeat(ticketCode, newSeatNumber);
        return ResponseEntity.ok("Kursi berhasi dipindah");
    }

    @PostMapping("/{ticketCode}/refund")
    public ResponseEntity<String> refundTicket(@PathVariable String ticketCode) {
        ticketService.adminRefundTicket(ticketCode);
        return ResponseEntity.ok("Refund tiket berhasil");
    }
}
