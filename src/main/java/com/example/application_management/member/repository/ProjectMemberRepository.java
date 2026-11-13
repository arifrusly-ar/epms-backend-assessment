package com.example.application_management.member.repository;

import com.example.application_management.member.entity.ProjectMember;
import com.example.application_management.member.enums.ProjectRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    List<ProjectMember> findByProjectId(Long projectId);

    boolean existsByProject_IdAndAppUser_IdAndRole(
            Long projectId,
            Long userId,
            ProjectRole role
    );

    //untuk mengelakkan seorang pengguna ditambah dua kali ke project yang sama.
    boolean existsByProject_IdAndAppUser_Id(Long projectId, Long userId);
}
