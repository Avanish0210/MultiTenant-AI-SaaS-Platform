package com.ProjectAI.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

import static com.ProjectAI.enums.ProjectPermission.*;


@RequiredArgsConstructor
@Getter
public enum ProjectRole {
    EDITOR(VIEW , EDIT , DELETE , VIEW_MEMBER),
    VIEWER(Set.of(VIEW , VIEW_MEMBER)),
    OWNER(Set.of(VIEW ,EDIT , DELETE , MANAGE_MEMBERS, VIEW_MEMBER));

    ProjectRole(ProjectPermission... permissions) {
        this.permissions = Set.of(permissions);
    }

    private final Set<ProjectPermission> permissions;
}