package com.monster.npd.governance.pdf.service;

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
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.monster.npd.governance.pdf.helpers.CP0PageTemplate;
import com.monster.npd.governance.pdf.helpers.CP1PageTemplate;
import com.monster.npd.governance.pdf.helpers.GovernanceAuditSummary;
import com.monster.npd.governance.pdf.helpers.GovernanceVolumeSummary;
import com.monster.npd.governance.pdf.helpers.PDfGenerationHelpers;
import com.monster.npd.governance.pdf.helpers.PageHeader;
import com.monster.npd.governance.pdf.pojo.CP;

@Service
public class PdfGeneratorService {

	private static final Logger logger = LogManager.getLogger(PdfGeneratorService.class);

	@Autowired
	CP0PageTemplate cp0PageTemplate;
	@Autowired
	CP1PageTemplate cp1PageTemplate;
	@Autowired
	GovernanceAuditSummary governanceAuditSummary;
	@Autowired
	GovernanceVolumeSummary governanceVolumeSummary;

	public Image loadImage(String filePath) {
		try (InputStream input = getClass().getResourceAsStream(filePath)) {
			if (input == null) {
				throw new IOException("Image not found at specified path.");
			}
			return Image.getInstance(input.readAllBytes());
		} catch (IOException | BadElementException e) {
			logger.error("Error loading image: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * To generate the CP pdf
	 * 
	 * @return bytes array
	 */
	public byte[] generatePdf(List<CP> cp) {
		Document document = new Document(PageSize.A4.rotate());
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {

			PDfGenerationHelpers generationHelpers = new PDfGenerationHelpers();
			PdfWriter writer = PdfWriter.getInstance(document, out);
			writer.setPageEvent(generationHelpers);
			document.open();
			PageHeader.addPageHeaders(document);
			initializeCP(document, cp);
			initalizeGovernanceAuditAndVolumeSummary(document, cp);

		} catch (Exception e) {
			logger.error("Error in creating CPs PDF : {}", e.getMessage(), e);
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
	public void initalizeGovernanceAuditAndVolumeSummary(Document document, List<CP> cp) {
		try {
			document.newPage();

			PDfGenerationHelpers.addTitle(document, "Governance Audit Summary", false);
			governanceAuditSummary.addGovernanceAuditSummary(document);
			PDfGenerationHelpers.addTitle(document, "Governance Volume Summary", false);
			governanceVolumeSummary.governanceVolumeSummary(document, cp);

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
	public void initializeCP(Document document, List<CP> cpList) {
		try {
			if (cpList.size() <= 0) {
				throw new DocumentException("Atleast one cpList is required to generate PDF.");
			}
			cpList.forEach(cpObj -> {

				try {
					initializeCPDocument(document, cpObj);
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
	private void initializeCPDocument(Document document, CP cpObj) throws DocumentException {

		String cpName = cpObj.getCpName();

		boolean isSamePreviousCP = Optional.ofNullable(cpObj.getIsSamePreviourCP()).orElse(false);
		boolean isActiveCP = Optional.ofNullable(cpObj.getIsActive()).orElse(false);

		if (isSamePreviousCP) {
			document.newPage();
			PDfGenerationHelpers.addTitle(document, cpName.concat(" is same as previous CP"), false);
			return;
		}

		switch (cpName.toUpperCase()) {
		case "CP-0":
			addCP0Content(document, isActiveCP);
			break;
		case "CP-1":
			addCP1Content(document, cpName, isActiveCP);
			break;
		case "CP-2":
			addCP1Content(document, cpName, isActiveCP);
			break;
		case "CP-3":
			addCP1Content(document, cpName, isActiveCP);
			break;
		case "CP-4":
			addCP1Content(document, cpName, isActiveCP);
			break;

		default:
			logger.warn("Unrecognized CP name: {}", cpName);
		}
	}

	/**
	 * this is to intialzie the CP0 template
	 * 
	 * @param document
	 * @throws DocumentException
	 */
	private void addCP0Content(Document document, boolean isActive) throws DocumentException {
		PDfGenerationHelpers.addTitle(document, "CP-0", isActive);
		cp0PageTemplate.addMarketScopeTableForCp0(document);
		cp0PageTemplate.addDeliverablesVSThresholdForCp0(document);
		PDfGenerationHelpers.addChunkParagraph(document, "What is the Project? :", "Some project details");
		PDfGenerationHelpers.addChunkParagraph(document, "What is the commercial benefit of approving this project? :",
				"Some commercial benefit details");
		cp0PageTemplate.addPortFolioStartegyForCp0(document);
		PDfGenerationHelpers.addChunkParagraph(document, "Approver comments :", "Some comments from approver");
	}

	/**
	 * This is to initalize the cp1 template
	 * 
	 */
	private void addCP1Content(Document document, String cpName, boolean isActive) throws DocumentException {
		document.newPage();
		PDfGenerationHelpers.addTitle(document, cpName, isActive);
		cp1PageTemplate.addMarketScopeTableForCp1(document);
		cp1PageTemplate.addDeliverablesVSThresholdForCp1(document);
		PDfGenerationHelpers.addChunkParagraph(document, "Project Managers comments :",
				"Some comments from Project Managers");
		PDfGenerationHelpers.addChunkParagraph(document, "Approver comments :", "Some comments from approver");
	}

}
