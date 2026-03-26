package com.bootcamp.ticketing_api.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventCreateRequest {
    @NotBlank(message = "Nama event wajib diisi")
    private String name;

    @NotBlank(message = "Lokasi wajib diisi")
    private String location;

    @NotNull(message = "Tanggal event wajib diisi")
    @Future(message = "Tanggal event harus di masa depan")
    private LocalDateTime eventDate;

    private String description;

    @NotEmpty(message = "Minimal harus ada satu kategori tiket")
    private List<CategoryCreateRequest> categories;
}
