package com.ProjectAI.dto.member;

import com.ProjectAI.enums.ProjectRole;

public record InviteMemberRequest(
        String email,
        ProjectRole role
) {
}
