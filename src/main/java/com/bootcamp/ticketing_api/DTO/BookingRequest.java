package com.bootcamp.ticketing_api.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingRequest {
    @NotNull(message = "ID Kategori harus diisi")
    private Long eventCategoryId;
}