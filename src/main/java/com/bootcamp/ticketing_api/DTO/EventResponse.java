package com.bootcamp.ticketing_api.DTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
class EventResponse {
    private Long id;
    private String name;
    private String location;
    private LocalDateTime eventDate;
    private String status;
}
