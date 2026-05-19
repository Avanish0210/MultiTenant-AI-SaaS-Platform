package com.ProjectAI.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Preview {

    Long id;

    Long projectId;

    String namespace;
    String prodName;
    String previewUrl;

    String status;

    Instant startedAt;
    Instant terminatedAt;

    Instant createdAt;
}
