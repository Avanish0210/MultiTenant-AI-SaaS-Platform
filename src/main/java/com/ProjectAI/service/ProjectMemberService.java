package com.ProjectAI.service;

import com.ProjectAI.dto.member.InviteMemberRequest;
import com.ProjectAI.dto.member.MemberResponse;
import com.ProjectAI.entity.ProjectMember;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface ProjectMemberService {
    @Nullable List<ProjectMember> getProjectMembers(Long projectId, Long userId);

    @Nullable ProjectMember inviteMember(Long projectId, InviteMemberRequest request, Long userId);

    @Nullable MemberResponse updateMemberRolee(Long projectId, Long memberId, InviteMemberRequest request, Long userId);

    @Nullable MemberResponse deleteProjectMember(Long projectId, Long memberId, Long userId);
}
