package com.itext.pdf.generation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itext.pdf.generation.service.PdfGeneratorService;

@RestController
@RequestMapping("/pdf")
public class PdfController {

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @GetMapping("/generate")
    public ResponseEntity<byte[]> generatePdf() {
        byte[] pdfBytes = pdfGeneratorService.generatePdf();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Student_Info.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
