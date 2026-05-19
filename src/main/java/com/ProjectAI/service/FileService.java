package com.ProjectAI.service;

import com.ProjectAI.dto.project.FileNode;
import org.jspecify.annotations.Nullable;

public interface FileService {
    @Nullable FileNode getFileTree(Long projectId, Long userId);

    @Nullable FileNode getFileContent(Long projectId, String path, Long userId);
}
