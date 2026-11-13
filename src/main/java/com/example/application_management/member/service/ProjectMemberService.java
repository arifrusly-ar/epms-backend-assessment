package com.example.application_management.member.service;

import com.example.application_management.member.dto.ProjectMemberRequest;
import com.example.application_management.member.dto.ProjectMemberResponse;
import com.example.application_management.member.entity.ProjectMember;
import com.example.application_management.member.repository.ProjectMemberRepository;
import com.example.application_management.project.entity.Project;
import com.example.application_management.project.enums.ProjectStatus;
import com.example.application_management.project.exception.InvalidProjectStateException;
import com.example.application_management.project.exception.ProjectNotFoundException;
import com.example.application_management.project.repository.ProjectRepository;
import com.example.application_management.security.entity.AppUser;
import com.example.application_management.security.repository.AppUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final AppUserRepository appUserRepository;

    //initialize
    public ProjectMemberService(ProjectMemberRepository projectMemberRepository,
                                ProjectRepository projectRepository,
                                AppUserRepository appUserRepository) {
        this.projectMemberRepository = projectMemberRepository;
        this.projectRepository = projectRepository;
        this.appUserRepository = appUserRepository;
    }

    @Transactional
    public ProjectMemberResponse addMember(Long projectId, ProjectMemberRequest request) {

        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new ProjectNotFoundException(projectId)
        );

        if (ProjectStatus.COMPLETED == project.getStatus()) {
            throw new InvalidProjectStateException("Cannot add member to completed project");
        }

        AppUser user = appUserRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!user.isEnabled()) {
            throw new IllegalArgumentException("user is disabled");
        }

        if (projectMemberRepository.existsByProject_IdAndAppUser_Id(projectId, user.getId())) {
            throw new IllegalStateException("User is already member of this project");
        }

        ProjectMember member = new ProjectMember();

        member.setName(request.getName());
        member.setEmail(request.getEmail());
        member.setRole(request.getRole());

        //establish relationship
        member.setProject(project);

        ProjectMember savedMember = projectMemberRepository.save(member);

        return mapToResponse(savedMember);
    }

    @Transactional(readOnly = true)
    public List<ProjectMemberResponse> getMembersByProject(Long projectId) {

        if (!projectRepository.existsById(projectId)) {
            throw new ProjectNotFoundException(projectId);
        }

        return projectMemberRepository.findByProjectId(projectId).stream()
                .map(this::mapToResponse).toList();
    }

    private ProjectMemberResponse mapToResponse(ProjectMember member) {

        return new ProjectMemberResponse(
                member.getId(),
                member.getName(),
                member.getEmail(),
                member.getRole(),
                member.getProject().getId(),
                member.getProject().getProjectNo()
        );
    }
}
