package com.bootcamp.ticketing_api.service;

import com.bootcamp.ticketing_api.DTO.EventReportResponse;
import com.bootcamp.ticketing_api.DTO.GlobalReportResponse;
import com.bootcamp.ticketing_api.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final TransactionRepository transactionRepository;

    public EventReportResponse getReportByEvent(Long eventId) {
        // Menghitung uang berdasarkan kategori yang statusnya SOLD (Flowchart Image 10)
        Double revenue = transactionRepository.getTotalRevenueByEvent(eventId);
        if (revenue == null)
            revenue = 0.0;

        return EventReportResponse.builder()
                .totalRevenue(revenue)
                .build();
    }

    public GlobalReportResponse getGlobalSummary(String startDateStr, String endDateStr) {
        java.time.LocalDateTime startDate = parseDateTime(startDateStr, java.time.LocalDateTime.now().minusYears(1), true);
        java.time.LocalDateTime endDate = parseDateTime(endDateStr, java.time.LocalDateTime.now(), false);

        java.util.List<com.bootcamp.ticketing_api.entity.Transaction> transactions = transactionRepository
                .findAllByDateRange(startDate, endDate);

        double totalRevenue = 0.0;
        long totalSold = 0;
        java.util.Map<String, com.bootcamp.ticketing_api.DTO.RevenueReportResponse> monthlyMap = new java.util.TreeMap<>();

        for (com.bootcamp.ticketing_api.entity.Transaction tx : transactions) {
            double amount = tx.getAmountPaid();
            long count = 1;

            if (tx.getTransactionType() == com.bootcamp.ticketing_api.entity.Transaction.TransactionType.REFUND) {
                amount = -amount;
                count = -1;
            }

            totalRevenue += amount;
            totalSold += count;

            String monthKey = tx.getCreatedAt().getYear() + "-"
                    + String.format("%02d", tx.getCreatedAt().getMonthValue());
            com.bootcamp.ticketing_api.DTO.RevenueReportResponse monthData = monthlyMap.getOrDefault(monthKey,
                    new com.bootcamp.ticketing_api.DTO.RevenueReportResponse(monthKey, 0.0, 0L));
            monthData.setTotalRevenue(monthData.getTotalRevenue() + amount);
            monthData.setTotalTicketsSold(monthData.getTotalTicketsSold() + count);
            monthlyMap.put(monthKey, monthData);
        }

        return GlobalReportResponse.builder()
                .totalGlobalRevenue(totalRevenue)
                .totalTicketsSold(totalSold)
                .monthlyTrends(new java.util.ArrayList<>(monthlyMap.values()))
                .build();
    }

    private java.time.LocalDateTime parseDateTime(String dateStr, java.time.LocalDateTime defaultDate, boolean isStart) {
        if (dateStr == null || dateStr.isEmpty()) {
            return defaultDate;
        }
        try {
            if (dateStr.contains("T")) {
                return java.time.LocalDateTime.parse(dateStr);
            } else {
                java.time.LocalDate date = java.time.LocalDate.parse(dateStr);
                return isStart ? date.atStartOfDay() : date.atTime(java.time.LocalTime.MAX);
            }
        } catch (Exception e) {
            return defaultDate;
        }
    }
}
