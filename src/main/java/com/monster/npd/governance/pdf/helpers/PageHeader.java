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
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.PdfPTable;
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
					BaseColor.LIGHT_GRAY);
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
			return id.flatMap(repositoryFunction).map(displayNameExtractor).orElse("-");
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
		    return Optional.ofNullable(dto)
		        .map(d -> {
		            return switch (label) {
		                case "Project Name" -> d.getProjectName();
		                case "Project Type" -> d.getProjectType();
		                case "Project Sub-type" -> d.getProjectSubType();
		                case "Program Tag" -> d.getProgramTag();
		                case "Reporting Quarter" -> d.getReportingQ();
		                case "Currently Active Stage" -> d.getCurrentActiveCP();
		                case "EZE PM" -> d.getE2ePm();
		                case "Last CP Date" -> d.getLastCPdate();
		                default -> "Unknown";
		            };
		        })
		        .orElse("-");
		}


}
