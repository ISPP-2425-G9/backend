package com.caronte.caronte.util.exceptions;

import com.caronte.caronte.util.ErrorHandler;

import lombok.Getter;

@Getter
public class ErrorHandlerException extends RuntimeException {

    private ErrorHandler errorHandler;

    public ErrorHandlerException(ErrorHandler errorHandler) {
        super();
        this.errorHandler = errorHandler;
    }
}
