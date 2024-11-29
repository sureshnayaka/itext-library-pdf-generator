package com.monster.npd.governance.pdf.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(PdfGenerationException.class)
	public ResponseEntity<Map<String, String>> handlePdfGenerationException(PdfGenerationException ex) {
		Map<String, String> errorResponse = new HashMap<>();
		errorResponse.put("status", "error");
		errorResponse.put("message", ex.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
