package com.ProjectAI.controller;

import com.ProjectAI.dto.project.FileNode;
import com.ProjectAI.service.ProjectFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/{projectId}/files")
public class FileController {

    private final ProjectFileService projectFileService;

    @GetMapping
    public ResponseEntity<FileNode> getFileTree(@PathVariable Long projectId){
        Long userId = 1L;
        return ResponseEntity.ok(projectFileService.getFileTree(projectId , userId));
    }

    @GetMapping("/{*path}")  // /src/hooks/get-user-hook.jsx
    public ResponseEntity<FileNode> getFile(@PathVariable Long projectId, @PathVariable String path){
        Long userId = 1L;
        return ResponseEntity.ok(projectFileService.getFileContent(projectId ,path ,  userId));

    }


}
