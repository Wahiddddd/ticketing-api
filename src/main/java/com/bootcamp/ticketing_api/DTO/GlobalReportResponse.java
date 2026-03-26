package com.bootcamp.ticketing_api.DTO;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class GlobalReportResponse {
    private Double totalGlobalRevenue;
    private Long totalTicketsSold;
    private List<RevenueReportResponse> monthlyTrends; // Data per bulan
}
