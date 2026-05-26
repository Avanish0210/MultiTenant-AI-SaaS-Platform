package com.ProjectAI.service.impl;

import com.ProjectAI.dto.project.ProjectRequest;
import com.ProjectAI.dto.project.ProjectResponse;
import com.ProjectAI.dto.project.ProjectSummaryResponse;
import com.ProjectAI.entity.Project;
import com.ProjectAI.entity.ProjectMember;
import com.ProjectAI.entity.ProjectMemberId;
import com.ProjectAI.entity.User;
import com.ProjectAI.enums.ProjectRole;
import com.ProjectAI.error.ResourceNotFoundException;
import com.ProjectAI.mapper.ProjectMapper;
import com.ProjectAI.repository.ProjectMemberRepository;
import com.ProjectAI.repository.ProjectRepository;
import com.ProjectAI.repository.UserRepository;
import com.ProjectAI.service.ProjectService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;
    private final ProjectMemberRepository projectMemberRepository;


    @Override
    public List<ProjectSummaryResponse> getUserProjects(Long userId) {
        var project = projectRepository.findAllAccessibleByUser(userId);
        return projectMapper.toListProjectSummaryResponse(project);
    }

    @Override
    public ProjectResponse getUserProjectById(Long id, Long userId) {
        Project project = getAccessibleProjectById(id , userId);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    public ProjectResponse createProject(ProjectRequest projectRequest, Long userId) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));
        Project project = Project.builder()
                .name(projectRequest.name())
                .isPublic(false)
                .build();
        project = projectRepository.save(project);

        ProjectMemberId projectMemberId = new ProjectMemberId(project.getId(), owner.getId());
        ProjectMember projectMember = ProjectMember.builder()
                .id(projectMemberId)
                .projectRole(ProjectRole.OWNER)
                .user(owner)
                .acceptedAt(Instant.now())
                .invitedAt(Instant.now())
                .project(project)
                .build();
        projectMemberRepository.save(projectMember);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    public ProjectResponse updateProejct(Long id, ProjectRequest projectRequest, Long userId) {

        Project project = getAccessibleProjectById(id , userId);

        project.setName(projectRequest.name());
        project = projectRepository.save(project);

        return projectMapper.toProjectResponse(project);
    }

    @Override
    public void softDelete(Long id, Long userId) {
        Project project = getAccessibleProjectById(id , userId);

        project.setDeletedAt(Instant.now());
        projectRepository.save(project);

    }

    public Project getAccessibleProjectById(Long projectId , Long userId) {
        return projectRepository.findAccessibleProjectById(projectId,userId).orElseThrow(
                () -> new ResourceNotFoundException("Project", projectId.toString())
        );
    }
}
