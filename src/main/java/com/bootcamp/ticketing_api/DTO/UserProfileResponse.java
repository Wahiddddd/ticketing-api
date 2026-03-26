package com.bootcamp.ticketing_api.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileResponse {
    private String username;
    private String email;
    private Double balance;
    private String role;
}