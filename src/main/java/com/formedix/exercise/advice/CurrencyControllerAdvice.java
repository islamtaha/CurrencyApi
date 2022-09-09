package com.formedix.exercise.advice;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.formedix.exercise.exceptions.BadRequestException;
import com.formedix.exercise.exceptions.NotFoundException;
import com.formedix.exercise.responses.ErrorResponse;

@ControllerAdvice
public class CurrencyControllerAdvice {

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFoundExceptions(NotFoundException e) {
        HttpStatus status = HttpStatus.NOT_FOUND; 
		return new ResponseEntity<>(
            new ErrorResponse(status, e.getMessage()),
            status
        );
	}
	
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ErrorResponse> handleMissingParams(MissingServletRequestParameterException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST; 
	    return new ResponseEntity<>(
            new ErrorResponse(status, e.getParameterName() + " parameter is missing"),
            status
        );
	}
	
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleMismatchType(MethodArgumentTypeMismatchException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
	    return new ResponseEntity<>(
            new ErrorResponse(status,  e.getName() + " does not match the expected type"),
            status
        );
	}
	
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleMismatchType(ConstraintViolationException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
	    return new ResponseEntity<>(
            new ErrorResponse(status, e.getMessage()),
            status
        );
	}
	
	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ErrorResponse> handleBadRequestExceptions(BadRequestException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST; 
		return new ResponseEntity<>(
            new ErrorResponse(status, e.getMessage()),
            status
        );
	}
	
	@ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleExceptions(Exception e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; 
        return new ResponseEntity<>(
            new ErrorResponse(status, "Internal server error - Please reach out to the server administrator to resolve the issue"),
            status
        );
    }
}
