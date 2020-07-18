package com.pedzich.aleksandra.library.exceptions;

import org.hibernate.PropertyValueException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.EntityNotFoundException;

@ControllerAdvice
public class LibraryExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<LibraryExceptionResponse> handleException(PropertyValueException exc) {
        LibraryExceptionResponse libraryExceptionResponse = new LibraryExceptionResponse(exc.getMessage());
        return ResponseEntity.badRequest().body(libraryExceptionResponse);
    }

    @ExceptionHandler
    public ResponseEntity<LibraryExceptionResponse> handleException(EntityNotFoundException exc) {
        LibraryExceptionResponse libraryExceptionResponse = new LibraryExceptionResponse(exc.getMessage());
        return ResponseEntity.status(404).body(libraryExceptionResponse);
    }
}