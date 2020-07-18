package com.pedzich.aleksandra.library.exceptions;

import lombok.Data;

@Data
public class LibraryExceptionResponse {

    private String message;

    public LibraryExceptionResponse() {}

    public LibraryExceptionResponse(String message) {
        this.message = message;
    }
}
