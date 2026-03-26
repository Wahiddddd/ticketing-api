package com.bootcamp.ticketing_api.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventReportResponse {
    private Double totalRevenue;
}
