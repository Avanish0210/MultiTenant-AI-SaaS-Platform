package com.ProjectAI.mapper;

import com.ProjectAI.dto.project.FileNode;
import com.ProjectAI.entity.ProjectFile;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectFileMapper {

    List<FileNode> toListOfFileNode(List<ProjectFile> projectFileList);
}
