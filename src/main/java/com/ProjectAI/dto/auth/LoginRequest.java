package com.ProjectAI.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotNull @Email String email ,
        @Size(min = 4 , max = 58) String password
) {
}
