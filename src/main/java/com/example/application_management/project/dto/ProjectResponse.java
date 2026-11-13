package com.example.application_management.project.dto;

import com.example.application_management.project.enums.ProjectStatus;

import java.time.LocalDate;

public class ProjectResponse {

    private Long id;
    private String projectNo;
    private String projectName;
    private String clientName;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private ProjectStatus status;

    public ProjectResponse() {
    }

    public ProjectResponse(
            Long id,
            String projectNo,
            String projectName,
            String clientName,
            String location,
            LocalDate startDate,
            LocalDate endDate,
            ProjectStatus status) {

        this.id = id;
        this.projectNo = projectNo;
        this.projectName = projectName;
        this.clientName = clientName;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getProjectNo() {
        return projectNo;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getClientName() {
        return clientName;
    }

    public String getLocation() {
        return location;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public ProjectStatus getStatus() {
        return status;
    }
}