package com.pedzich.aleksandra.library.exceptions;

import lombok.Data;

@Data
public class LibraryExceptionResponse {

    private String errorMessage;

    public LibraryExceptionResponse() {}

    public LibraryExceptionResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
