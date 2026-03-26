package com.bootcamp.ticketing_api.controller;

import com.bootcamp.ticketing_api.DTO.EventReportResponse;
import com.bootcamp.ticketing_api.DTO.GlobalReportResponse;
import com.bootcamp.ticketing_api.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    // Laporan pendapatan per event
    @GetMapping("/event/{eventId}")
    public ResponseEntity<EventReportResponse> getEventReport(@PathVariable Long eventId) {
        return ResponseEntity.ok(reportService.getReportByEvent(eventId));
    }

    // Laporan ringkasan global (Summary) dengan filter periode
    @GetMapping("/summary")
    public ResponseEntity<GlobalReportResponse> getGlobalSummary(
            @org.springframework.web.bind.annotation.RequestParam(required = false) String startDate,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String endDate) {
        return ResponseEntity.ok(reportService.getGlobalSummary(startDate, endDate));
    }
}
