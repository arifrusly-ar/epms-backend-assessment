package com.example.application_management.member.dto;

import com.example.application_management.member.enums.ProjectRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ProjectMemberRequest {

    @NotBlank(message = "Member name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    private String email;

    @NotNull(message = "Role is required")
    private ProjectRole role;

    @NotNull
    Long userId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ProjectRole getRole() {
        return role;
    }

    public void setRole(ProjectRole role) {
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
