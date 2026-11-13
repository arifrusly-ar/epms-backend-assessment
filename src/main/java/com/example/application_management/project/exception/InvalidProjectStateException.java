package com.example.application_management.project.exception;

public class InvalidProjectStateException extends RuntimeException{

    public InvalidProjectStateException(String message) {
        super(message);
    }
}
