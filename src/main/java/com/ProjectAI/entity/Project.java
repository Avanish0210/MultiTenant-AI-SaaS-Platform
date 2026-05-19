package com.ProjectAI.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Project {


    Long id;

    String name;
    User owner;

    Boolean isPublic=false;

    Instant createdAt;
    Instant updatedAt;
    Instant deletedAt;
}
