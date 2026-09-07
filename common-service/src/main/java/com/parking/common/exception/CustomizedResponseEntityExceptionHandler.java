package com.parking.common.exception;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public final ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}

	@ExceptionHandler(BadCredentialsException.class)
	public final ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid Credentials");
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex,
			HttpHeaders headers,
			HttpStatusCode status,
			WebRequest request) {
		Map<String, String> errors = new HashMap<>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			errors.put(error.getField(), error.getDefaultMessage());
		}
		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(SQLIntegrityConstraintViolationException.class)
	protected ResponseEntity<Object> handleSQLIntegrityException(Exception ex) {
		Map<String, String> errors = new HashMap<>();
		errors.put("message", "Email already exists, Try another email");
		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}
}
