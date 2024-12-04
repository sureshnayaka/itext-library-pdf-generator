package com.monster.npd.governance.pdf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.monster.npd.governance.pdf.pojo.ErrorResponse;
import com.monster.npd.governance.pdf.service.impl.PdfGeneratorServiceImplementation;

@RestController
@RequestMapping("/pdf")
public class PdfController {

	@Autowired
	private PdfGeneratorServiceImplementation pdfGeneratorService;

	
	@GetMapping("/generate/{requestId}")
	public ResponseEntity<?> generatePdf(@PathVariable long requestId) {
		try {
			HttpHeaders headers = new HttpHeaders();
			byte[] pdfBytes = pdfGeneratorService.generatePdf(requestId, headers);

			return ResponseEntity.ok().headers(headers).body(pdfBytes);
		} catch (Exception e) {
			ErrorResponse errorResponse = new ErrorResponse("error", "Failed to generate PDF: " + e.getMessage());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}


}
