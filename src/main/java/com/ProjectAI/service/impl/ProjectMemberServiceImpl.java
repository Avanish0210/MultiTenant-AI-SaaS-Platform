package com.ProjectAI.service.impl;

import com.ProjectAI.dto.member.InviteMemberRequest;
import com.ProjectAI.dto.member.MemberResponse;
import com.ProjectAI.dto.member.UpdateMemberRoleRequest;
import com.ProjectAI.entity.Project;
import com.ProjectAI.entity.ProjectMember;
import com.ProjectAI.entity.ProjectMemberId;
import com.ProjectAI.entity.User;
import com.ProjectAI.mapper.ProjectMemberMapper;
import com.ProjectAI.repository.ProjectMemberRepository;
import com.ProjectAI.repository.ProjectRepository;
import com.ProjectAI.repository.UserRepository;
import com.ProjectAI.service.ProjectMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class ProjectMemberServiceImpl implements ProjectMemberService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberMapper projectMemberMapper;
    private final ProjectMemberRepository projectMemberRepository;

    @Override
    public List<MemberResponse> getProjectMembers(Long projectId, Long userId) {
        Project project = getAccessibleProjectById(projectId , userId);

        List<MemberResponse> memberResponseList = new ArrayList<>();
        memberResponseList.add(projectMemberMapper.toProjectMemberResponseFromOwner(project.getOwner()));

        memberResponseList.addAll(
                projectMemberRepository.findByIdProjectId(projectId)
                        .stream()
                        .map(projectMemberMapper::toProjectMemberResponseFromMember)
                        .toList());

        return memberResponseList;
    }

    @Override
    public MemberResponse inviteMember(Long projectId, InviteMemberRequest request, Long userId) {

        Project project = getAccessibleProjectById(projectId ,userId);

        if(!project.getOwner().getId().equals(userId)){
            throw new RuntimeException("Not allowed");
        }

        User invitedUser = userRepository.findByEmail(request.email()).orElseThrow();

        if(invitedUser.getId().equals(userId)) {
            throw new RuntimeException("Cannot invite yourself");
        }

        ProjectMemberId  projectMemberId = new ProjectMemberId(projectId,invitedUser.getId());

        ProjectMember projectMember = ProjectMember.builder()
                .id(projectMemberId)
                .project(project)
                .user(invitedUser)
                .projectRole(request.role())
                .invitedAt(Instant.now())
                .build();

        projectMemberRepository.save(projectMember);
        return projectMemberMapper.toProjectMemberResponseFromMember(projectMember);
    }

    @Override
    public MemberResponse updateMemberRolee(Long projectId, Long memberId, UpdateMemberRoleRequest request, Long userId) {
        Project project = getAccessibleProjectById(projectId ,userId);

        if(!project.getOwner().getId().equals(userId)){
            throw new RuntimeException("Not allowed");
        }

        ProjectMemberId projectMemberId = new ProjectMemberId(projectId,memberId);
        ProjectMember projectMember = projectMemberRepository.findById(projectMemberId).orElseThrow();

        projectMember.setProjectRole(request.role());
        projectMemberRepository.save(projectMember);

        return projectMemberMapper.toProjectMemberResponseFromMember(projectMember);

    }

    @Override
    public void removeProjectMember(Long projectId, Long memberId, Long userId) {
        Project project = getAccessibleProjectById(projectId ,userId);

        if(!project.getOwner().getId().equals(userId)){
            throw new RuntimeException("Not allowed");
        }

        ProjectMemberId projectMemberId = new ProjectMemberId(projectId,memberId);
        if(!projectMemberRepository.existsById(projectMemberId)){
            throw new RuntimeException("Not found");
        }
        projectMemberRepository.deleteById(projectMemberId);

    }

    public Project getAccessibleProjectById(Long projectId , Long userId) {
        return projectRepository.findAccessibleProjectById(projectId,userId).orElseThrow();
    }
}
