package com.bootcamp.ticketing_api.DTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EventUpdateRequest {
    private String name;
    private String location;
    private LocalDateTime eventDate;
    private String description;
    private java.util.List<CategoryCreateRequest> categories;
}
