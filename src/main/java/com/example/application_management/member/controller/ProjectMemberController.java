package com.example.application_management.member.controller;

import com.example.application_management.member.dto.ProjectMemberRequest;
import com.example.application_management.member.dto.ProjectMemberResponse;
import com.example.application_management.member.service.ProjectMemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/members")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    public ProjectMemberController(ProjectMemberService projectMemberService) {
        this.projectMemberService = projectMemberService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProjectMemberResponse> addMember (@PathVariable Long projectId,
                                                            @Valid @RequestBody ProjectMemberRequest request) {

        ProjectMemberResponse member = projectMemberService.addMember(projectId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(member);
    }

    @GetMapping
    public ResponseEntity<List<ProjectMemberResponse>> getMemberByProject(@PathVariable Long projectId) {

        List<ProjectMemberResponse>members = projectMemberService.getMembersByProject(projectId);

        return ResponseEntity.ok(members);
    }
}
