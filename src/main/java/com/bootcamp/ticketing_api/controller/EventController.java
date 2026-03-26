package com.bootcamp.ticketing_api.controller;

import com.bootcamp.ticketing_api.DTO.EventDetailResponse;
import com.bootcamp.ticketing_api.DTO.EventSimpleResponse;
import com.bootcamp.ticketing_api.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    // GET /api/events?search=konser&page=0&size=10
    @GetMapping
    public ResponseEntity<Page<EventSimpleResponse>> getAllEvents(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) com.bootcamp.ticketing_api.entity.Event.EventStatus status,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime startDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime endDate,
            Pageable pageable) {
        return ResponseEntity.ok(eventService.getAllEvents(search, status, startDate, endDate, pageable));
    }

    // GET /api/events/1
    @GetMapping("/{id}")
    public ResponseEntity<EventDetailResponse> getEventDetail(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventDetail(id));
    }
}