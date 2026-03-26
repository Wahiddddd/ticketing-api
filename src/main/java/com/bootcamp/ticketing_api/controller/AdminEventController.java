package com.bootcamp.ticketing_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import com.bootcamp.ticketing_api.DTO.EventCreateRequest;
import com.bootcamp.ticketing_api.DTO.EventUpdateRequest;
import com.bootcamp.ticketing_api.service.AdminEventService;

@RestController
@RequestMapping("/api/admin/events")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
@RequiredArgsConstructor
public class AdminEventController {
    private final AdminEventService adminEventService;

    @GetMapping
    public ResponseEntity<org.springframework.data.domain.Page<com.bootcamp.ticketing_api.DTO.EventSimpleResponse>> getAllEvents(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) com.bootcamp.ticketing_api.entity.Event.EventStatus status,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime startDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime endDate,
            org.springframework.data.domain.Pageable pageable) {
        return ResponseEntity.ok(adminEventService.getAllEvents(search, status, startDate, endDate, pageable));
    }

    @PostMapping
    public ResponseEntity<String> createEvent(@Valid @RequestBody EventCreateRequest request, java.security.Principal principal) {
        adminEventService.saveEvent(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body("Event berhasil dibuat");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateEvent(@PathVariable Long id, @Valid @RequestBody EventUpdateRequest request) {
        adminEventService.updateEvent(id, request);
        return ResponseEntity.ok("Event berhasil diperbarui");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEvent(@PathVariable Long id) {
        adminEventService.deleteEvent(id);
        return ResponseEntity.ok("Event berhasil dihapus");
    }
}
