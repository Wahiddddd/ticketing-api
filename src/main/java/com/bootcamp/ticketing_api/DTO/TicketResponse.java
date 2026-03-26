package com.bootcamp.ticketing_api.DTO;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class TicketResponse {
    private Long id;
    private String ticketCode;
    private String eventName;
    private String categoryName;
    private String seatNumber;
    private LocalDateTime eventDate;
    private String location;
    private String status; // SOLD, BROKEN, REFUNDED (Enum String)
}