package com.example.application_management.document.exception;

public class DuplicateDocumentException extends RuntimeException{

    public DuplicateDocumentException(String documentNo) {
        super("Document number " + documentNo + " already exist in this project");
    }
}
