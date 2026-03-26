package com.bootcamp.ticketing_api.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CategoryCreateRequest {
    @NotBlank(message = "Nama kategori (misal: VIP/Reguler) wajib diisi")
    private String name;

    @NotBlank(message = "Kode kategori (misal: VVIP) wajib diisi")
    private String code;

    @NotNull(message = "Harga wajib diisi")
    @Min(value = 0, message = "Harga tidak boleh negatif")
    private Double price;

    @NotNull(message = "Kuota wajib diisi")
    @Min(value = 1, message = "Kuota minimal 1")
    private Integer quota;
}
