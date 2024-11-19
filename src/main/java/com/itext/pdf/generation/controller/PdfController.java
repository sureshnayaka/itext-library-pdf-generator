package com.itext.pdf.generation.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itext.pdf.generation.pojo.CP;
import com.itext.pdf.generation.pojo.MarketScope;
import com.itext.pdf.generation.pojo.SubmissionGovernanceAudit;
import com.itext.pdf.generation.repository.DeliverableThresholdRepository;
import com.itext.pdf.generation.repository.MarketScopeRepository;
import com.itext.pdf.generation.repository.SubmissionGovernanceAuditRepository;
import com.itext.pdf.generation.repository.SubmissionGovernanceMilestoneRepository;
import com.itext.pdf.generation.service.PdfGeneratorService;
import com.itextpdf.text.DocumentException;

@RestController
@RequestMapping("/pdf")
public class PdfController {

	@Autowired
	private PdfGeneratorService pdfGeneratorService;

	@Autowired
	private MarketScopeRepository marketScopeRepository;

	@Autowired
	private DeliverableThresholdRepository deliverableThresholdRepository;

	@Autowired
	private SubmissionGovernanceAuditRepository auditRepository;

	@Autowired
	private SubmissionGovernanceMilestoneRepository submissionGovernanceMilestoneRepository;

	@PostMapping("/generate")
	public ResponseEntity<byte[]> generatePdf(@RequestBody List<CP> cp) throws DocumentException, IOException {
		byte[] pdfBytes = pdfGeneratorService.generatePdf(cp);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		String fileName = "ProjectNumber-LeadMarket-Brand-Platform-Variant-SkuDetail-" + "CP-0_1_2" + "-"
				+ new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".pdf";

		headers.setContentDispositionFormData("attachment", fileName);

		return ResponseEntity.ok().headers(headers).body(pdfBytes);
	}

	@GetMapping("/marktet")
	public ResponseEntity<List<MarketScope>> getMArketScope() {
		return ResponseEntity.ok().body(marketScopeRepository.findAll());

	}

	@GetMapping("/deliverable")
	public ResponseEntity<List<?>> getDeliverableScope() {
		return ResponseEntity.ok().body(submissionGovernanceMilestoneRepository.findAll());

	}

}
