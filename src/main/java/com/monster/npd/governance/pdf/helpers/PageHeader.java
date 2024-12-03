package com.monster.npd.governance.pdf.helpers;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.monster.npd.governance.pdf.pojo.PageHeaderDetailsDTO;
import com.monster.npd.governance.pdf.pojo.ProgramTag;
import com.monster.npd.governance.pdf.pojo.ProjectSubType;
import com.monster.npd.governance.pdf.pojo.ProjectType;
import com.monster.npd.governance.pdf.pojo.ReportingQuarter;
import com.monster.npd.governance.pdf.pojo.SubmissionRequest;
import com.monster.npd.governance.pdf.pojo.SubmissionRequestGovernanceMilestone;
import com.monster.npd.governance.pdf.pojo.UserIdentity;
import com.monster.npd.governance.pdf.repository.ProgramTagRepository;
import com.monster.npd.governance.pdf.repository.ProjectSubTypeRepository;
import com.monster.npd.governance.pdf.repository.ProjectTypeRepository;
import com.monster.npd.governance.pdf.repository.ReportingQuarterRepository;
import com.monster.npd.governance.pdf.repository.SubmissionRequestGovernanceMilestoneRepository;
import com.monster.npd.governance.pdf.repository.UserIdentityRepository;
import com.monster.npd.governance.pdf.utils.Utils;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.*;


@Component
public class PageHeader {

	private static final Logger logger = LogManager.getLogger(PageHeader.class);

	@Autowired
	private SubmissionRequestGovernanceMilestoneRepository submissionRequestGovernanceMilestoneRepository;

	@Autowired
	private ProjectTypeRepository projectTypeRepository;

	@Autowired
	private ProjectSubTypeRepository projectSubTypeRepository;

	@Autowired
	private ProgramTagRepository programTagRepository;
	@Autowired
	private ReportingQuarterRepository reportingQuarter;
	@Autowired
	private UserIdentityRepository identityRepository;

	@Autowired
	CpDataProcessor cpDataProcessor;

	public static void addPageHeaders(Document document, PageHeaderDetailsDTO headerDetailsDTO)
			throws DocumentException, IllegalArgumentException, IllegalAccessException {
//		PDfGenerationHelpers.addTitle(document, "Governance PDF Summary", false);
		float[] columnWidths = { 2f, 3f };
		PdfPTable table = new PdfPTable(columnWidths);
		table.setWidthPercentage(50);
		table.setHorizontalAlignment(Element.ALIGN_CENTER);
		List<String> labels = List.of("Project Name", "Project Type", "Project Sub-type", "Program Tag",
				"Reporting Quarter", "Currently Active Stage", "EZE PM", "Last CP Date");

		Font labelFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, Font.BOLD);
		Font valueFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 9);
		for (String label : labels) {
			String value = getValueFromDTO(label, headerDetailsDTO);
			PDfGenerationHelpers.addTableRow(table, label, value, labelFont, valueFont, Element.ALIGN_CENTER, 2,
					Color.LIGHT_GRAY);
		}

		document.add(table);

	}

	public PageHeaderDetailsDTO getHeaderData(Long requestId) {
		if (Utils.isNullOrEmptyLong(requestId)) {
			logger.error("Request Id is null or empty.");
			return null;
		}

		try {
			// Fetch request data
			List<SubmissionRequestGovernanceMilestone> requestData = submissionRequestGovernanceMilestoneRepository
					.findByIdRequestId(requestId);

			if (requestData.isEmpty() || Utils.isNullOrEmptyObject(requestData.get(0).getSubmissionRequest())) {
				logger.error("Null or empty Request!!");
				return null;
			}

			// Extract submissionRequest
			SubmissionRequest submissionRequest = requestData.get(0).getSubmissionRequest();

			// Fetch related data using helper methods
			String projectType = getProjectType(submissionRequest.getRPoCpProjectTypeId());
			String projectSubType = getSubProjectType(submissionRequest.getRPoProjectSubTypeId());
			String programTag = getProgramTag(submissionRequest.getRPoProgramTagId());
			String reportingQuarter = getReportingQuarter(submissionRequest.getRPoPmDeliveryQuarterId());
			String e2ePMName = getUserName(submissionRequest.getRPoE2ePmId());
			String cpLastActiveDate = cpDataProcessor.getLastCPDate(requestId);

			// Create DTO object
			PageHeaderDetailsDTO pageHeaderDetailsDTO = new PageHeaderDetailsDTO(submissionRequest.getProjectName(),
					projectType, projectSubType, programTag, reportingQuarter,
					"CP-" + submissionRequest.getCurrentCheckpoint(), e2ePMName, cpLastActiveDate);

			logger.debug("PageHeaderDetailsDTO: {}", new ObjectMapper().writeValueAsString(pageHeaderDetailsDTO));

			return pageHeaderDetailsDTO;

		} catch (JsonProcessingException e) {
			logger.error("Error occurred while processing JSON: {}", e.getMessage());
		} catch (Exception e) {
			logger.error("Error occurred while fetching header details: {}", e.getMessage());
		}

		return null;
	}

	private <T> String getDisplayName(Optional<Long> id, Function<Long, Optional<T>> repositoryFunction,
			Function<T, String> displayNameExtractor) {
		try {
			return id.flatMap(repositoryFunction).map(displayNameExtractor).orElse("N/A");
		} catch (Exception e) {
			logger.error("Error occurred while fetching display name: {}", e.getMessage(), e);
			return "";
		}
	}

	public String getProjectType(Long projectTypeId) {
		return getDisplayName(Optional.ofNullable(projectTypeId), projectTypeRepository::findById,
				ProjectType::getDisplayName);
	}

	public String getSubProjectType(Long projectSubTypeId) {
		return getDisplayName(Optional.ofNullable(projectSubTypeId), projectSubTypeRepository::findById,
				ProjectSubType::getDisplayName);
	}

	public String getProgramTag(Long programTagId) {
		return getDisplayName(Optional.ofNullable(programTagId), programTagRepository::findById,
				ProgramTag::getDisplayName);
	}

	public String getReportingQuarter(Long reportingQuarterId) {
		return getDisplayName(Optional.ofNullable(reportingQuarterId), reportingQuarter::findById,
				ReportingQuarter::getDisplayName);
	}

	public String getUserName(Long e2epmId) {
		return getDisplayName(Optional.ofNullable(e2epmId), identityRepository::findById, UserIdentity::getDisplayName);
	}

	private static String getValueFromDTO(String label, PageHeaderDetailsDTO dto) {
		return Optional.ofNullable(dto).map(d -> {
			return switch (label) {
			case "Project Name" -> d.getProjectName();
			case "Project Type" -> d.getProjectType();
			case "Project Sub-type" -> d.getProjectSubType();
			case "Program Tag" -> d.getProgramTag();
			case "Reporting Quarter" -> d.getReportingQ();
			case "Currently Active Stage" -> d.getCurrentActiveCP();
			case "EZE PM" -> d.getE2ePm();
			case "Last CP Date" -> d.getLastCPdate();
			default -> "N/A";
			};
		}).orElse("-");
	}

	public String setMainHeading(PdfWriter writer, Document document, Long requestId) {
		String heading = "";
		try {
			SubmissionRequest submissionRequest = cpDataProcessor.getSubmissionRequest(requestId);
			if (!Utils.isNullOrEmptyObject(submissionRequest)) {
				// Safely handle null for each object in the chain
				String market = getDisplayNameOrDefault(
						submissionRequest.getLeadMarket() != null ? submissionRequest.getLeadMarket().getDisplayName()
								: null);
				String brand = getDisplayNameOrDefault(
						submissionRequest.getBrands() != null ? submissionRequest.getBrands().getDisplayName() : null);
				String platform = getDisplayNameOrDefault(
						submissionRequest.getPlatforms() != null ? submissionRequest.getPlatforms().getDisplayName()
								: null);
				String variant = getDisplayNameOrDefault(
						submissionRequest.getVariantSku() != null ? submissionRequest.getVariantSku().getDisplayName()
								: null);
				String packageType = getDisplayNameOrDefault(submissionRequest.getPackagingType() != null
						? submissionRequest.getPackagingType().getDisplayName()
						: null);

				heading = market + "_" + brand + "_" + platform + "_" + variant + "_" + packageType;
				float x = document.right() - 250;
				float y = document.top() - 90;
				PdfContentByte canvas = writer.getDirectContent();

				canvas.beginText();
				BaseFont baseFont = BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
				canvas.setFontAndSize(baseFont, 14);
				canvas.setColorFill(Color.WHITE);
				canvas.showTextAligned(Element.ALIGN_CENTER, heading, x - 250, y + 100, 0);
				canvas.endText();
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return heading;

	}

	private String getDisplayNameOrDefault(String displayName) {
		return (displayName == null || displayName.isEmpty()) ? "N/A" : displayName;
	}

}
