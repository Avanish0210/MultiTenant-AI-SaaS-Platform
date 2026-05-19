package com.ProjectAI.service;

import com.ProjectAI.dto.project.ProjectRequest;
import com.ProjectAI.dto.project.ProjectResponse;
import com.ProjectAI.dto.project.ProjectSummaryResponse;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface ProjectService {
    @Nullable List<ProjectSummaryResponse> getUserProjects(Long userId);

    @Nullable ProjectResponse getUserProjectById(Long id, Long userId);

    @Nullable ProjectResponse createProject(ProjectRequest projectRequest, Long userId);

    @Nullable ProjectResponse updateProejct(Long id, ProjectRequest projectRequest, Long userId);

    void softDelete(Long id, Long userId);
}
