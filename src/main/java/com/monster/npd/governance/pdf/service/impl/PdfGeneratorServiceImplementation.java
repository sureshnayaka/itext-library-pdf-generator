package com.monster.npd.governance.pdf.service.impl;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.monster.npd.governance.pdf.helpers.CPPageTemplate;
import com.monster.npd.governance.pdf.helpers.CpDataProcessor;
import com.monster.npd.governance.pdf.helpers.GovernanceAuditSummaryHelper;
import com.monster.npd.governance.pdf.helpers.GovernanceVolumeSummary;
import com.monster.npd.governance.pdf.helpers.PDfGenerationHelpers;
import com.monster.npd.governance.pdf.helpers.PageHeader;
import com.monster.npd.governance.pdf.pojo.CP;
import com.monster.npd.governance.pdf.pojo.PageHeaderDetailsDTO;
import com.monster.npd.governance.pdf.pojo.SubmissionRequest;
import com.monster.npd.governance.pdf.service.PdfGeneratorInterface;
import com.monster.npd.governance.pdf.utils.Utils;

@Service
public class PdfGeneratorServiceImplementation implements PdfGeneratorInterface {

	private static final Logger logger = LogManager.getLogger(PdfGeneratorServiceImplementation.class);

	private static final String CP0 = "CP0";
	private static final String CP1 = "CP1";
	private static final String WHY_PROJECT = "What is the Project? :";
	private static final String ABOUT_PROJECT = "What is the commercial benefit of approving this project? :";
	private static final String APPROVER_COMMENTS = "Approver comments :";
	private static final String PM_COMMENTS = "Project Managers comments :";
	private static final String GAS = "Governance Audit Summary";
	private static final String GVS = "Governance Volume Summary";

	private static final String SAME_PREVIOUS_CP = " is same as previous CP";

	@Autowired
	CPPageTemplate cpPageTemplate;
	@Autowired
	GovernanceAuditSummaryHelper governanceAuditSummary;
	@Autowired
	GovernanceVolumeSummary governanceVolumeSummary;
	@Autowired
	PageHeader pageHeader;
	@Autowired
	CpDataProcessor cpHelper;

	/**
	 * To generate the CP pdf
	 * 
	 * @return bytes array
	 * @throws Exception
	 */
	public byte[] generatePdf(long requestId) {

		Document document = new Document(PageSize.A4.rotate());
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {
			if (Utils.isNullOrEmptyLong(requestId)) {
				throw new NullPointerException("Empty request id");
			}

			List<CP> cp = cpHelper.getCpByRequestId(requestId);

			// Check if the cp list is null or empty and throw an error
			if (cp == null || cp.isEmpty()) {
				throw new NullPointerException("CP list is null or empty for requestId: " + requestId);
			}

			PDfGenerationHelpers generationHelpers = new PDfGenerationHelpers();
			PdfWriter writer = PdfWriter.getInstance(document, out);
			writer.setPageEvent(generationHelpers);
			document.open();

			PageHeaderDetailsDTO pageHeaderDetailsDTO = pageHeader.getHeaderData(requestId);
			PageHeader.addPageHeaders(document, pageHeaderDetailsDTO);

			// Initialize the CP data in the document
			initializeCP(document, cp, requestId);

			// Initialize Governance and Volume Summary in the document
			initalizeGovernanceAuditAndVolumeSummary(document, requestId);
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
	 * Initalize Aduit and volume summary on page end
	 * 
	 * @param document
	 */
	public void initalizeGovernanceAuditAndVolumeSummary(Document document, long requestId) {
		try {
			document.newPage();

			PDfGenerationHelpers.addTitle(document, GAS, false);
			governanceAuditSummary.addGovernanceAuditSummary(document, requestId);
			if (cpHelper.isApprovedCp(requestId)) {
				PDfGenerationHelpers.addTitle(document, GVS, false);
				governanceVolumeSummary.governanceVolumeSummary(document, requestId);
			}
			System.err.println(cpHelper.isApprovedCp(requestId));

		} catch (DocumentException | JsonProcessingException e) {
			logger.error("Error in initalizing PDF Summary : {}", e.getMessage(), e);
		} finally {
			document.close();
		}
	}

	/**
	 * Initalize CP to print on the document based on the input list of CP
	 * 
	 * @param document
	 * @param cpList
	 */
	public void initializeCP(Document document, List<CP> cpList, long requestId) {
		try {
			if (cpList.size() <= 0) {
				throw new DocumentException("Atleast one cpList is required to generate PDF.");
			}
			cpList.forEach(cpObj -> {

				try {
					initializeCPDocument(document, cpObj, requestId);
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
	private void initializeCPDocument(Document document, CP cpObj, long requestId) throws DocumentException {

		String cpName = cpObj.getCpName();

		boolean isSamePreviousCP = Optional.ofNullable(cpObj.getIsSamePreviourCP()).orElse(false);
		boolean isActiveCP = Optional.ofNullable(cpObj.getIsActive()).orElse(false);

		if (isSamePreviousCP) {
			document.newPage();
			PDfGenerationHelpers.addTitle(document, cpName.concat(SAME_PREVIOUS_CP), false);
			return;
		}
		addCPContent(document, isActiveCP, requestId, cpName);
	}

	/**
	 * this is to intialzie the CP0 template
	 * 
	 * @param document
	 * @throws DocumentException
	 */
	private void addCPContent(Document document, boolean isActive, long requestId, String cpName)
			throws DocumentException {
		if (!cpName.equalsIgnoreCase(CP0)) {
			document.newPage();
		}

		PDfGenerationHelpers.addTitle(document, cpName, isActive);
		cpPageTemplate.addMarketScopeTableForCp(document, requestId, cpName);
		cpPageTemplate.addDeliverablesVSThresholdForCp(document, requestId, cpName);

		if (cpName.equalsIgnoreCase(CP0)) {
			addProjectDetails(document, requestId);
		} else {
			PDfGenerationHelpers.addChunkParagraph(document, PM_COMMENTS, "Some comments from Project Managers");
		}

		if (cpName.equalsIgnoreCase(CP0) || cpName.equalsIgnoreCase(CP1)) {
			cpPageTemplate.addPortFolioStartegyForCp0(document, requestId);
		}

		PDfGenerationHelpers.addChunkParagraph(document, APPROVER_COMMENTS, "Some comments from approver");
	}

	private void addProjectDetails(Document document, long requestId) {

		Optional<SubmissionRequest> submissionRequestOpt = cpHelper.getProjectDetailsForCP0(requestId);
		submissionRequestOpt.ifPresent(submissionRequest -> {
			try {

				PDfGenerationHelpers.addChunkParagraph(document, WHY_PROJECT,
						" " + cpPageTemplate.getValueOrDefault(Optional.ofNullable(submissionRequest.getWhyProject())));
				PDfGenerationHelpers.addChunkParagraph(document, ABOUT_PROJECT, " "
						+ cpPageTemplate.getValueOrDefault(Optional.ofNullable(submissionRequest.getProjectAbout())));
			} catch (DocumentException e) {
				logger.error("Error occured in fetching project comments {}", e.getMessage());
			}
		});
	}

}
