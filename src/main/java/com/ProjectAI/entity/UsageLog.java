package com.ProjectAI.entity;

import com.fasterxml.jackson.annotation.JsonTypeId;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.security.Timestamp;
import java.time.Instant;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class UsageLog {

    Long id;
    User user;
    Project project;

    String action;

    Integer tokensUsed;
    Integer durationMs;

    String metaData; // JSON of {model_used, prompt_used},

    Instant createdAt;

}
