package com.bootcamp.ticketing_api.DTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EventSimpleResponse {
    private Long id;
    private String name;
    private String location;
    private LocalDateTime eventDate;
    private Double startingPrice; // Harga terendah dari semua kategori
    private String status; // UPCOMING, SOLD OUT, dll
}