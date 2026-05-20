package com.ProjectAI.mapper;

import com.ProjectAI.dto.project.ProjectResponse;
import com.ProjectAI.dto.project.ProjectSummaryResponse;
import com.ProjectAI.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectResponse toProjectResponse(Project project);

    @Mapping(target = "projectName" , source = "name")
    ProjectSummaryResponse toProjectSummaryResponse(Project project);

    List<ProjectSummaryResponse> toListProjectSummaryResponse(List<Project> projects);
}
