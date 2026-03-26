package com.bootcamp.ticketing_api.DTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingResponse {
    private String ticketCode;
    private String seatNumber;
    private String eventName;
    private String categoryName;
    private Double amountPaid;
    private LocalDateTime transactionTime;
}
