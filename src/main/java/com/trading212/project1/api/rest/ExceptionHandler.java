package com.trading212.project1.api.rest;

import com.trading212.project1.api.rest.errors.UserErrorResponse;
import com.trading212.project1.core.exceptions.CredentialsIntegrityException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice
public class ExceptionHandler extends ResponseEntityExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(CredentialsIntegrityException.class)
    public ResponseEntity<UserErrorResponse> handleDataIntegrityViolation(CredentialsIntegrityException e) {
        UserErrorResponse errorResponse = new UserErrorResponse();
        errorResponse.setDescription(e.getMessage());
        errorResponse.setTimestamp(System.currentTimeMillis());
        errorResponse.setStatus(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    @org.springframework.web.bind.annotation.ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<UserErrorResponse> handleInvalidDataViolation(SQLIntegrityConstraintViolationException e) {
        UserErrorResponse errorResponse = new UserErrorResponse();
        errorResponse.setDescription("try again");
        errorResponse.setTimestamp(System.currentTimeMillis());
        errorResponse.setStatus(400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

}
