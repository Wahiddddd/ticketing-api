package com.bootcamp.ticketing_api.DTO;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class EventDetailResponse {
    private Long id;
    private String name;
    private String description;
    private String location;
    private LocalDateTime eventDate;
    private String status;
    private List<CategoryResponse> categories; // Daftar VIP, Reguler, dll
}
