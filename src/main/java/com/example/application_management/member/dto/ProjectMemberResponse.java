package com.example.application_management.member.dto;

import com.example.application_management.member.enums.ProjectRole;

public class ProjectMemberResponse {

    private Long id;
    private String name;
    private String email;
    private ProjectRole role;

    private Long projectId;
    private String projectNo;

    public ProjectMemberResponse(
            Long id,
            String name,
            String email,
            ProjectRole role,
            Long projectId,
            String projectNo) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.projectId = projectId;
        this.projectNo = projectNo;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public ProjectRole getRole() {
        return role;
    }

    public Long getProjectId() {
        return projectId;
    }

    public String getProjectNo() {
        return projectNo;
    }
}
