package com.example.application_management.project.exception;

public class DuplicateProjectException extends  RuntimeException{

    public DuplicateProjectException(String projectNo) {
        super("Project Number " + projectNo + " already exists");
    }
}
