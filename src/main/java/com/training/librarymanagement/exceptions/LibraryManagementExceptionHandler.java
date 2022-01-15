package com.training.librarymanagement.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;

@ControllerAdvice
public class LibraryManagementExceptionHandler {

    Logger LOG = LoggerFactory.getLogger(LibraryManagementExceptionHandler.class);

    @ExceptionHandler(SQLException.class)
    protected ResponseEntity<Object> handleSqlException(SQLException ex) {

        LOG.warn("Handling SQL Exceptions [{}]", ex.getMessage());

        ErrorResponse response = new ErrorResponse();
        response.setStatus(HttpStatus.CONFLICT);
        response.setMessage(ex.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }
}
