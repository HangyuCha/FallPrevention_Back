package com.metaverse.dbfall.user.dto;

import jakarta.validation.constraints.Size;

public class UpdateUserRequest {
    @Size(min = 1, max = 100)
    public String username;
    // ISO-8601 yyyy-MM-dd
    public String birthDate;
}