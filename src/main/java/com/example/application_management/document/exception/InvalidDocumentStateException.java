package com.example.application_management.document.exception;

public class InvalidDocumentStateException extends RuntimeException{

    public InvalidDocumentStateException(String message) {
        super(message);
    }
}
