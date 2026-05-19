package com.ProjectAI.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.security.Timestamp;
import java.time.Instant;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProjectFile {

    Long id;
    Project projectId;
    String path;
    String minioObjectKey;
    User createdBy;
    User  updatedBy;
    Instant createdAt;
    Instant updatedAt;

}
