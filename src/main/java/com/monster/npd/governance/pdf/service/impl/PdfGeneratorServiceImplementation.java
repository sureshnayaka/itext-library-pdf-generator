package com.monster.npd.governance.pdf.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.monster.npd.governance.pdf.helpers.CPPageTemplate;
import com.monster.npd.governance.pdf.helpers.CpDataProcessor;
import com.monster.npd.governance.pdf.helpers.GovernanceAuditSummaryHelper;
import com.monster.npd.governance.pdf.helpers.GovernanceVolumeSummary;
import com.monster.npd.governance.pdf.helpers.PDfGenerationHelpers;
import com.monster.npd.governance.pdf.helpers.PageHeader;
import com.monster.npd.governance.pdf.pojo.CP;
import com.monster.npd.governance.pdf.service.PdfGeneratorInterface;
import com.monster.npd.governance.pdf.utils.Utils;

@Service
public class PdfGeneratorServiceImplementation implements PdfGeneratorInterface {

	private static final Logger logger = LogManager.getLogger(PdfGeneratorServiceImplementation.class);

	private static final String CP0 = "CP0";
	private static final String CP1 = "CP1";
	private static final String WHY_PROJECT = "Why do the Project? :";
	private static final String WHAT_PROJECT = "What do this project? :";
	private static final String APPROVER_COMMENTS = "Approver comments :";
	private static final String PM_COMMENTS = "Project Managers comments :";
	private static final String GAS = "Governance Audit Summary";
	private static final String GVS = "Governance Volume Summary";

	private static final String SAME_PREVIOUS_CP = " is same as previous CP";

	private static final String LINK_STRATEGY = "Link to stratergy";

	private static final String CM_COMMENTS = "Commercial comments";

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
	@Autowired
	PDfGenerationHelpers pDfGenerationHelpers;

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

			// Initialize the CP data in the document
			initializeCP(document, cp, requestId, writer);

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
			addSpacer(document);
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
	public void initializeCP(Document document, List<CP> cpList, long requestId, PdfWriter writer) {
		try {
			if (cpList.size() <= 0) {
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

		boolean isSamePreviousCP = Optional.ofNullable(cpObj.getIsSamePreviourCP()).orElse(false);
		boolean isActiveCP = Optional.ofNullable(cpObj.getIsActive()).orElse(false);

		if (isSamePreviousCP) {
			document.newPage();
			PDfGenerationHelpers.addTitle(document, cpName.concat(SAME_PREVIOUS_CP), false);
			return;
		}
		addCPContent(document, isActiveCP, requestId, cpName, writer);
	}

	public Image loadImage(String filePath) {
		try (InputStream input = getClass().getResourceAsStream(filePath)) {
			if (!Utils.isNullOrEmptyObject(input)) {
				return Image.getInstance(input.readAllBytes());
			}
		} catch (IOException | BadElementException e) {
			logger.error("Error loading image: " + e.getMessage());
		}
		return null;
	}

	/**
	 * this is to intialzie the CP0 template
	 * 
	 * @param document
	 * @throws DocumentException
	 */
	private void addCPContent(Document document, boolean isActive, long requestId, String cpName, PdfWriter writer)
			throws DocumentException {
		if (!cpName.equalsIgnoreCase(CP0)) {
			document.newPage();
		}
		pDfGenerationHelpers.setCPHeaderTitleOnTopRight(cpName, document, writer);
		pDfGenerationHelpers.setCPHeaderImage(cpName, document, writer);
		pDfGenerationHelpers.setMainHeading(writer, document);

		addSpacer(document);
		PDfGenerationHelpers.addChunkComments(document, WHAT_PROJECT, " ", 50, 2.5f, 0.05f);
		PDfGenerationHelpers.addChunkComments(document, WHY_PROJECT, " ", 50, 2.5f, 0.05f);
		PDfGenerationHelpers.addChunkComments(document, LINK_STRATEGY, " ", 50, 2.5f, 0.05f);

		if (!cpName.equalsIgnoreCase(CP0)) {
			cpPageTemplate.addAnualizedSummaryCheckPoints(document, requestId, cpName);
		}
		PDfGenerationHelpers.addChunkHeaderCheckPoint(document, cpName, " ");
		cpPageTemplate.addMarketScopeTableForCp(document, requestId, cpName);
		cpPageTemplate.addDeliverablesVSThresholdForCp(document, requestId, cpName);

		if (cpName.equalsIgnoreCase(CP0)) {
			cpPageTemplate.addPortFolioStartegyForCp0(document, requestId);
		}

		if (!cpName.equalsIgnoreCase(CP0)) {
			PDfGenerationHelpers.addChunkComments(document, PM_COMMENTS, " ", 100, 1f, 0.015f);
			PDfGenerationHelpers.addChunkComments(document, CM_COMMENTS, "From “Commercial Rational for changes", 100,
					1f, 0.015f);
		}

		PDfGenerationHelpers.addChunkComments(document, APPROVER_COMMENTS, "Some comments from approver", 100, 1f,
				0.015f);

	}

	public void addSpacer(Document document) {

		try {
			Paragraph title = new Paragraph(" ", new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.BOLD));
			title.setSpacingBefore(10f);
			document.add(title);
		} catch (DocumentException e) {
			e.printStackTrace();
		}

	}
}

//private void addProjectDetails(Document document, long requestId) {
//
//	Optional<SubmissionRequest> submissionRequestOpt = cpHelper.getProjectDetailsForCP0(requestId);
//	submissionRequestOpt.ifPresent(submissionRequest -> {
//		try {
//
//			PDfGenerationHelpers.addChunkParagraph(document, WHY_PROJECT,
//					" " + cpPageTemplate.getValueOrDefault(Optional.ofNullable(submissionRequest.getWhyProject())));
//			PDfGenerationHelpers.addChunkParagraph(document, ABOUT_PROJECT, " "
//					+ cpPageTemplate.getValueOrDefault(Optional.ofNullable(submissionRequest.getProjectAbout())));
//		} catch (DocumentException e) {
//			logger.error("Error occured in fetching project comments {}", e.getMessage());
//		}
//	});
//}
