package com.monster.npd.governance.pdf.service.impl;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;
import com.monster.npd.governance.pdf.helpers.CPPageTemplate;
import com.monster.npd.governance.pdf.helpers.CpDataProcessor;
import com.monster.npd.governance.pdf.helpers.PDfGenerationHelpers;
import com.monster.npd.governance.pdf.helpers.PageHeader;
import com.monster.npd.governance.pdf.pojo.CP;
import com.monster.npd.governance.pdf.pojo.SubmissionGovernanceMilestone;
import com.monster.npd.governance.pdf.pojo.SubmissionRequest;
import com.monster.npd.governance.pdf.pojo.SubmissionRequestGovernanceMilestone;
import com.monster.npd.governance.pdf.service.PdfGeneratorInterface;
import com.monster.npd.governance.pdf.utils.Utils;

@Service
public class PdfGeneratorServiceImplementation implements PdfGeneratorInterface {

	private static final Logger logger = LogManager.getLogger(PdfGeneratorServiceImplementation.class);

	private static final String CP0 = "CP0";
	private static final String WHY_PROJECT = "Why do the Project?";
	private static final String WHAT_PROJECT = "What do this project?";
	private static final String APPROVER_COMMENTS = "Approver comments";
	private static final String PM_COMMENTS = "Project Managers comments";
	private static final String SAME_PREVIOUS_CP = " is same as previous CP";
	private static final String LINK_STRATEGY = "Link to stratergy";
	private static final String CM_COMMENTS = "Commercial comments";

	@Autowired
	CPPageTemplate cpPageTemplate;
	@Autowired
	PageHeader pageHeader;
	@Autowired
	CpDataProcessor cpHelper;
	@Autowired
	PDfGenerationHelpers pDfGenerationHelpers;

	/**
	 * To generate the CP pdf
	 * 
	 * @return bytes array
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	public byte[] generatePdf(long requestId, HttpHeaders headers) {
	    Document document = new Document(PageSize.A4.rotate(), 32, 32, 32, 32); // margin 0.5 inches to remove the bleed
	    ByteArrayOutputStream out = new ByteArrayOutputStream();

	    if (Utils.isNullOrEmptyLong(requestId)) {
	        throw new NullPointerException("Empty request id");
	    }

	    List<CP> cp = cpHelper.getCPListForPdfGeneration(requestId);

	    // Check if the cp list is null or empty and throw an error
	    if (cp == null || cp.isEmpty()) {
	        throw new NullPointerException("CP list is null or empty for requestId: " + requestId);
	    }

	    try (out) {
	        PDfGenerationHelpers generationHelpers = new PDfGenerationHelpers();
	        PdfWriter writer = PdfWriter.getInstance(document, out);
	        writer.setPageEvent(generationHelpers);
	        document.open();
	        String header = cpHelper.getHeaderValues(requestId);
	        // Initialize the CP data in the document
	        initializeCP(document, cp, requestId, writer);

	        // TODO need to know how to name should be for PDF
	        headers.setContentType(MediaType.APPLICATION_PDF);
	        String fileName = requestId + "-" + header + "CP-0" + "-"
	                + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".pdf";

	        headers.setContentDispositionFormData("attachment", fileName);
	        logger.info("Successfully Generated PDF!!");

	    } catch (Exception e) {
	        logger.error("Error in creating CPs PDF : {}", e.getMessage(), e);
	        throw new NullPointerException(e.getMessage());
	    } finally {
	        document.close();
	    }

	    return out.toByteArray();
	}


	/**
	 * Initalize CP to print on the document based on the input list of CP
	 * 
	 * @param document
	 * @param cpList
	 */
	public void initializeCP(Document document, List<CP> cpList, long requestId, PdfWriter writer) {
		try {
			if (cpList.isEmpty()) {
				throw new DocumentException("Atleast one cpList is required to generate PDF.");
			}
			cpList.forEach(cpObj -> {

				try {
					initializeCPDocument(document, cpObj, requestId, writer);
				} catch (DocumentException e) {
					logger.error("Error initializing CP {}: {}", cpObj.getCpName(), e.getMessage(), e);
				}
			});
		} catch (DocumentException e) {
			logger.error("Error initializing CPs: {}", e.getMessage(), e);
		}
	}

	/**
	 * check which cp to print and ignore other based on the input
	 * 
	 * @param document
	 * @param cpObj
	 * @throws DocumentException
	 */
	private void initializeCPDocument(Document document, CP cpObj, long requestId, PdfWriter writer)
			throws DocumentException {

		String cpName = cpObj.getCpName();
		System.err.println(cpObj.getIsSamePreviourCP());
		boolean isSamePreviousCP = Optional.ofNullable(cpObj.getIsSamePreviourCP()).orElse(false);

		if (isSamePreviousCP || (Utils.isNullOrEmptyObject(cpObj.getIsSamePreviourCP())&& cpObj.getIsActive())) {
			document.newPage();
			PDfGenerationHelpers.addTitle(document, cpName.concat(SAME_PREVIOUS_CP), false);
			return;
		}
		addCPContent(document, requestId, writer, cpObj );
	}

	/**
	 * this is to intialzie the CP0 template
	 * 
	 * @param document
	 * @throws DocumentException
	 */
	private void addCPContent(Document document, long requestId, PdfWriter writer, CP cp) throws DocumentException {
		String cpName = cp.getCpName();
		String pmComments = cp.getPmComments();
		String cmComments = cp.getCommercialRelationalComments();
		String approverComments = cp.getApproverComments();
		if (!cpName.equalsIgnoreCase(CP0)) {
			document.newPage();
		}
		pageHeader.setMainHeading(writer, document, requestId);
		addProjectDetails(document, requestId);
		PDfGenerationHelpers.addChunkComments(document, LINK_STRATEGY, "N/A", 80, 1.25f, 0.015f);
		pDfGenerationHelpers.setCPHeaderImage(cpName, document, writer);

		pDfGenerationHelpers.setCPHeaderTitleOnTopRight(cpName, document, writer);

		if (!cpName.equalsIgnoreCase(CP0)) {
			cpPageTemplate.addAnualizedSummaryCheckPoints(document, requestId, cpName);
		}
		List<SubmissionRequestGovernanceMilestone> governanceMilestones = cpHelper.getCPApprovedDate(requestId, cpName);
		String approvedDate = getDecisionDateString(governanceMilestones);

		PDfGenerationHelpers.addChunkHeaderCheckPoint(document, cpName, approvedDate);// Apprvoed Date
		cpPageTemplate.addMarketScopeTableForCp(document, requestId, cpName);
		cpPageTemplate.addDeliverablesVSThresholdForCp(document, requestId, cpName);

		if (cpName.equalsIgnoreCase(CP0)|| cpName.equalsIgnoreCase("CP1")) {
			cpPageTemplate.addPortFolioStartegyForCp0(document, cp);

		}

		if (!cpName.equalsIgnoreCase(CP0)) {
			PDfGenerationHelpers.addChunkComments(document, PM_COMMENTS,
					!Utils.isNullOrEmptyString(pmComments) ? pmComments : "N/A", 100, 1f, 0.015f);
			PDfGenerationHelpers.addChunkComments(document, CM_COMMENTS,
					!Utils.isNullOrEmptyString(cmComments) ? cmComments : "N/A", 100, 1f, 0.015f);
		}

		PDfGenerationHelpers.addChunkComments(document, APPROVER_COMMENTS,
				!Utils.isNullOrEmptyString(approverComments) ? approverComments : "N/A", 100, 1f, 0.015f);

	}

	public static String getDecisionDateString(List<SubmissionRequestGovernanceMilestone> governanceMilestones) {
		if (governanceMilestones != null && !governanceMilestones.isEmpty()) {
			SubmissionRequestGovernanceMilestone firstMilestone = governanceMilestones.get(0);
			if (firstMilestone != null && firstMilestone.getGovernanceMilestone() != null) {
				SubmissionGovernanceMilestone milestone = firstMilestone.getGovernanceMilestone();

				if (milestone.getDecisionDate() != null) {
					return "Approved : " + CpDataProcessor.extractDate(milestone.getDecisionDate().toString());
				}
			}
		}

		return " ";
	}

	private void addProjectDetails(Document document, long requestId) {

		Optional<SubmissionRequest> submissionRequestOpt = cpHelper.getProjectDetailsForCP0(requestId);
		submissionRequestOpt.ifPresent(submissionRequest -> {
			try {
				PDfGenerationHelpers.addChunkComments(document, WHAT_PROJECT,
						" " + cpPageTemplate.getValueOrDefault(Optional.ofNullable(submissionRequest.getWhyProject())),
						80, 1.25f, 0.015f);
				PDfGenerationHelpers.addChunkComments(document, WHY_PROJECT,
						" " + cpPageTemplate
								.getValueOrDefault(Optional.ofNullable(submissionRequest.getProjectAbout())),
						80, 1.25f, 0.015f);

			} catch (DocumentException e) {
				logger.error("Error occured in fetching project comments {}", e.getMessage());
			}
		});
	}
}
