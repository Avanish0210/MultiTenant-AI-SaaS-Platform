package com.ProjectAI.service;

import com.ProjectAI.dto.member.InviteMemberRequest;
import com.ProjectAI.dto.member.MemberResponse;
import com.ProjectAI.dto.member.UpdateMemberRoleRequest;
import com.ProjectAI.entity.ProjectMember;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface ProjectMemberService {
    List<MemberResponse> getProjectMembers(Long projectId, Long userId);

    MemberResponse inviteMember(Long projectId, InviteMemberRequest request, Long userId);

    MemberResponse updateMemberRolee(Long projectId, Long memberId, UpdateMemberRoleRequest request, Long userId);

    void removeProjectMember(Long projectId, Long memberId, Long userId);
}
