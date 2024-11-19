package com.monster.npd.governance.pdf.controller;

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

import com.itextpdf.text.DocumentException;
import com.monster.npd.governance.pdf.pojo.CP;
import com.monster.npd.governance.pdf.pojo.MarketScope;
import com.monster.npd.governance.pdf.repository.DeliverableThresholdRepository;
import com.monster.npd.governance.pdf.repository.MarketScopeRepository;
import com.monster.npd.governance.pdf.repository.SubmissionGovernanceAuditRepository;
import com.monster.npd.governance.pdf.repository.SubmissionGovernanceMilestoneRepository;
import com.monster.npd.governance.pdf.repository.SubmissionRequestCommercialMarketScopeRepository;
import com.monster.npd.governance.pdf.repository.SubmissionRequestDeliverableThresholdRepository;
import com.monster.npd.governance.pdf.repository.SubmissionRequestGovernanceMilestoneRepository;
import com.monster.npd.governance.pdf.repository.SubmissionsRequestRepository;
import com.monster.npd.governance.pdf.service.PdfGeneratorService;

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
	
	@Autowired
	private SubmissionsRequestRepository submissionsRequestRepository;
	
	@Autowired
	private SubmissionRequestCommercialMarketScopeRepository commercialMarketScopeRepository;
	
	@Autowired
	private SubmissionRequestDeliverableThresholdRepository submissionRequestDeliverableThresholdRepository;
	
	@Autowired
	private SubmissionRequestGovernanceMilestoneRepository submissionRequestGovernanceMilestoneRepository;

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
		return ResponseEntity.ok().body(submissionsRequestRepository.findAll());

	}

}
