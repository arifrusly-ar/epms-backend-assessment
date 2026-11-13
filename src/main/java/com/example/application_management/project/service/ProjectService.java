package com.example.application_management.project.service;

import com.example.application_management.project.dto.ProjectRequest;
import com.example.application_management.project.dto.ProjectResponse;
import com.example.application_management.project.entity.Project;
import com.example.application_management.project.enums.ProjectStatus;
import com.example.application_management.project.exception.DuplicateProjectException;
import com.example.application_management.project.exception.InvalidProjectStateException;
import com.example.application_management.project.exception.ProjectNotFoundException;
import com.example.application_management.project.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<ProjectResponse> getAllProject() {
        return projectRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    public ProjectResponse createProject(ProjectRequest request) {

        //exception if project already exist
        if (projectRepository.existsByProjectNo(request.getProjectNo())) {
            throw new DuplicateProjectException(request.getProjectNo());
        }

        Project project = new Project();

        project.setProjectNo(request.getProjectNo());
        project.setProjectName(request.getProjectName());
        project.setClientName(request.getClientName());
        project.setLocation(request.getLocation());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());

        // Business rule
        project.setStatus(ProjectStatus.DRAFT);

        Project savedProject = projectRepository.save(project);

        return mapToResponse(savedProject);
    }

    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id).orElseThrow(
                () ->  new ProjectNotFoundException(id)
        );

        return mapToResponse(project);
    }

    /// update project
    public ProjectResponse updateProject(Long id, ProjectRequest request) {
        Project project = projectRepository.findById(id).orElseThrow(
                () -> new ProjectNotFoundException(id));

        //business rule
        if (ProjectStatus.COMPLETED == project.getStatus()) {
            throw new InvalidProjectStateException("Completed project cannot be updated");
        }

        project.setProjectNo(request.getProjectNo());
        project.setProjectName(request.getProjectName());
        project.setClientName(request.getClientName());
        project.setLocation(request.getLocation());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());

        Project updatedProject = projectRepository.save(project);

        return mapToResponse(updatedProject);
    }

    //activate project, transactional
    @Transactional
    public ProjectResponse activateProject(Long id) {
        Project project = projectRepository.findById(id).orElseThrow(
                () -> new ProjectNotFoundException(id)
        );

        if (project.getStatus() != ProjectStatus.DRAFT) {
            throw new InvalidProjectStateException("Only Draft Project Can Be Activated");
        }

        project.setStatus(ProjectStatus.ACTIVE);

        Project updatedProject = projectRepository.save(project);

        return mapToResponse(updatedProject);
    }

    //complete project , transactional
    @Transactional
    public ProjectResponse completeProject(Long id) {

        Project project = projectRepository.findById(id).orElseThrow(
                () -> new ProjectNotFoundException(id)
        );

        if (ProjectStatus.ACTIVE != project.getStatus()) {
            throw new InvalidProjectStateException("Only Active Project Can Be Completed");
        }

        project.setStatus(ProjectStatus.COMPLETED);

        Project updatedProject = projectRepository.save(project);

        return mapToResponse(updatedProject);
    }


    private ProjectResponse mapToResponse(Project project) {

        return new ProjectResponse(
                project.getId(),
                project.getProjectNo(),
                project.getProjectName(),
                project.getClientName(),
                project.getLocation(),
                project.getStartDate(),
                project.getEndDate(),
                project.getStatus()
        );
    }
}
