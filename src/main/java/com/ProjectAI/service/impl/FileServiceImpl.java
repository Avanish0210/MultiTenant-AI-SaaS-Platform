package com.ProjectAI.service.impl;

import com.ProjectAI.dto.project.FileNode;
import com.ProjectAI.service.FileService;
import org.springframework.stereotype.Service;

@Service
public class FileServiceImpl implements FileService {
    @Override
    public FileNode getFileTree(Long projectId, Long userId) {
        return null;
    }

    @Override
    public FileNode getFileContent(Long projectId, String path, Long userId) {
        return null;
    }
}
