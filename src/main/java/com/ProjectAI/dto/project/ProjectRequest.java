package com.ProjectAI.dto.project;

import jakarta.validation.constraints.NotNull;

public record ProjectRequest(
        @NotNull String name
) {
}
