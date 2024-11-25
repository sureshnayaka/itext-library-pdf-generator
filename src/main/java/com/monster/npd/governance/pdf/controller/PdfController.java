package com.monster.npd.governance.pdf.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.monster.npd.governance.pdf.pojo.ErrorResponse;
import com.monster.npd.governance.pdf.pojo.MarketScope;
import com.monster.npd.governance.pdf.repository.DeliverableThresholdRepository;
import com.monster.npd.governance.pdf.repository.MarketScopeRepository;
import com.monster.npd.governance.pdf.repository.SubmissionGovernanceAuditRepository;
import com.monster.npd.governance.pdf.repository.SubmissionGovernanceMilestoneRepository;
import com.monster.npd.governance.pdf.repository.SubmissionRequestCommercialMarketScopeRepository;
import com.monster.npd.governance.pdf.repository.SubmissionRequestDeliverableThresholdRepository;
import com.monster.npd.governance.pdf.repository.SubmissionRequestGovernanceMilestoneRepository;
import com.monster.npd.governance.pdf.repository.SubmissionRequestRepositoryCustom;
import com.monster.npd.governance.pdf.repository.SubmissionsRequestRepository;
import com.monster.npd.governance.pdf.service.impl.PdfGeneratorServiceImplementation;
import com.monster.npd.governance.pdf.utils.Utils;

@RestController
@RequestMapping("/pdf")
public class PdfController {

	@Autowired
	private PdfGeneratorServiceImplementation pdfGeneratorService;

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

	@Autowired
	private SubmissionRequestRepositoryCustom submissionRequestRepositoryCustom;

	@PostMapping("/generate/{requestId}")
	public ResponseEntity<?> generatePdf(@PathVariable long requestId) {
		try {
			byte[] pdfBytes = pdfGeneratorService.generatePdf(requestId);			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_PDF);
			String fileName = "ProjectNumber-LeadMarket-Brand-Platform-Variant-SkuDetail-" + "CP-0_1_2" + "-"
					+ new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".pdf";

			headers.setContentDispositionFormData("attachment", fileName);

			return ResponseEntity.ok().headers(headers).body(pdfBytes);
		} catch (Exception e) {
			ErrorResponse errorResponse = new ErrorResponse("error", "Failed to generate PDF: " + e.getMessage());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	@GetMapping("/marktet")
	public ResponseEntity<List<?>> getMArketScope() {
		return ResponseEntity.ok().body(submissionsRequestRepository.getAllSubmissionsById(16387L));

	}

	@GetMapping("/deliverable/{id}")
	public ResponseEntity<List<?>> getDeliverableScope(@PathVariable long id) {
		return ResponseEntity.ok().body(submissionRequestGovernanceMilestoneRepository.findByIdRequestId(id));

	}

}
