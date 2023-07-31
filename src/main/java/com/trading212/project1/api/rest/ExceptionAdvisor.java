package com.trading212.project1.api.rest;

import com.trading212.project1.api.rest.errors.ErrorResponse;
import com.trading212.project1.core.exceptions.CredentialsIntegrityException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice
public class ExceptionAdvisor extends ResponseEntityExceptionHandler {
    private static final int BAD_REQUEST_CODE = 400;
    private static final int CONFLICT_CODE = 409;

    @org.springframework.web.bind.annotation.ExceptionHandler(CredentialsIntegrityException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(CredentialsIntegrityException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setDescription(e.getMessage());
        errorResponse.setTimestamp(System.currentTimeMillis());
        errorResponse.setStatus(BAD_REQUEST_CODE);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    @org.springframework.web.bind.annotation.ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDataViolation(SQLIntegrityConstraintViolationException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setDescription("try with different data");
        errorResponse.setTimestamp(System.currentTimeMillis());
        errorResponse.setStatus(CONFLICT_CODE);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

}
