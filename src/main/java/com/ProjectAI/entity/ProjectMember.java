package com.ProjectAI.entity;

import com.ProjectAI.enums.ProjectRole;
import java.time.Instant;


public class ProjectMember {


    ProjectMemberId id;

    Project project;

    User user;

    ProjectRole projectRole;

    Instant invitedAt;
    Instant acceptedAt;
}
