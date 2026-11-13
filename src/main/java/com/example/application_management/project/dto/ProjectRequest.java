package com.example.application_management.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class ProjectRequest {

    @NotBlank(message = "Project number is required")
    @Size(max = 50, message = "Project no cannot exceed 50 character")
    private String projectNo;

    @NotBlank(message = "Project name is required")
    @Size(max = 200, message = "Project name cannot exceed 200 character")
    private String projectName;

    @NotBlank(message = "Client name is required")
    private String clientName;

    @NotBlank(message = "Location is required ")
    private String location;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate endDate;

    ///getter //setter
    public String getProjectNo() {
        return projectNo;
    }

    public void setProjectNo(String projectNo) {
        this.projectNo = projectNo;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
