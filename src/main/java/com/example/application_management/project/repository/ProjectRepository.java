package com.example.application_management.project.repository;

import com.example.application_management.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository
        extends JpaRepository<Project, Long> {

    //derived queruy method --> dont have to implement query, jpa will recognize itsef and build query
    boolean existsByProjectNo(String projectNo);
}
