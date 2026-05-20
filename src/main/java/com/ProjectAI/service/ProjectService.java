package com.ProjectAI.service;

import com.ProjectAI.dto.project.ProjectRequest;
import com.ProjectAI.dto.project.ProjectResponse;
import com.ProjectAI.dto.project.ProjectSummaryResponse;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface ProjectService {
    List<ProjectSummaryResponse> getUserProjects(Long userId);

    ProjectResponse getUserProjectById(Long id, Long userId);

    ProjectResponse createProject(ProjectRequest projectRequest, Long userId);

    ProjectResponse updateProejct(Long id, ProjectRequest projectRequest, Long userId);

    void softDelete(Long id, Long userId);
}
