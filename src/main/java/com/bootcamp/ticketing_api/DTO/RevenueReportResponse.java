package com.bootcamp.ticketing_api.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RevenueReportResponse {
    private String period; // Misal: "2024-03"
    private Double totalRevenue;
    private Long totalTicketsSold;
}
