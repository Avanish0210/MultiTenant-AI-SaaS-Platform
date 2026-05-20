package com.ProjectAI.service;

import com.ProjectAI.dto.project.FileNode;
import org.jspecify.annotations.Nullable;

public interface FileService {
    FileNode getFileTree(Long projectId, Long userId);

    FileNode getFileContent(Long projectId, String path, Long userId);
}
