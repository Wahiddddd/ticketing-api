package com.bootcamp.ticketing_api.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryResponse {
    private Long id;
    private String categoryName;
    private String categoryCode;
    private Double price;
    private Integer remainingCapacity;
}
