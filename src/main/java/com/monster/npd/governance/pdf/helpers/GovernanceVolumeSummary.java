package com.monster.npd.governance.pdf.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.monster.npd.governance.pdf.pojo.CheckpointData;

@Component
public class GovernanceVolumeSummary {

	@Autowired
	CpDataProcessor cpDataProcessor;

	public void governanceVolumeSummary(Document document, long requestId)
			throws DocumentException, JsonMappingException, JsonProcessingException {

		Font font = FontFactory.getFont(FontFactory.TIMES_BOLD, 9, BaseColor.WHITE);
		Font subCell = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL, BaseColor.BLACK);
		Font fontCell = FontFactory.getFont(FontFactory.TIMES_BOLD, 9, BaseColor.BLACK);

		Map<String, CheckpointData> commercialMarketScopes = cpDataProcessor.getCPforVolumeSummary(requestId);
		List<String> cpHeaders = new ArrayList<>();
		for (String key : commercialMarketScopes.keySet()) {
			cpHeaders.add(key);
		}

		if (commercialMarketScopes.isEmpty()) {
			return;
		}
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(commercialMarketScopes);

		JsonNode rootNode = objectMapper.readTree(json);

		// Extract metric names (e.g., anualizedValue, 3M volume, etc.)
		Iterator<String> metricKeys = rootNode.get(cpHeaders.get(0)).fieldNames(); // Assuming all CPs have the same
																					// keys

		List<List<String>> rowData = new ArrayList<>();
		while (metricKeys.hasNext()) {
			String metricKey = metricKeys.next();
			List<String> row = new ArrayList<>();

			for (Iterator<String> cpIterator = rootNode.fieldNames(); cpIterator.hasNext();) {
				String cpKey = cpIterator.next();
				JsonNode metricNode = rootNode.get(cpKey).get(metricKey);

				row.add(metricNode.get("value").asText());
				row.add(metricNode.get("change").asText());
			}

			// Add the row to the result
			rowData.add(row);
		}

		// Define first column values
		List<String> firstColumnValues = Arrays.asList("Annualised Value", "3M Volume", "NSV", "DP Date (Aligned)");

		// Calculate the total columns: 1 empty + (2 sub-headers per CP)
		int totalColumns = 1 + (cpHeaders.size() * 2);
		PdfPTable table = new PdfPTable(totalColumns);
		table.setWidthPercentage(99);
		table.setSpacingAfter(10f);

		// Add empty header cell for the first column
		table.addCell(new PdfPCell(new Phrase("")));

		// Add dynamic main headers for each CP
		IntStream.range(0, cpHeaders.size()).forEach(i -> {
			PdfPCell headerCell = new PdfPCell(new Phrase(cpHeaders.get(i), font));
			headerCell.setBackgroundColor(BaseColor.BLACK);
			headerCell.setColspan(2);
			headerCell.setBorderColorLeft(BaseColor.WHITE);
			headerCell.setBorderWidthLeft(1);
			headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(headerCell);
		});

		// Add sub-headers "Value" and "% Changes" for each CP
		table.addCell(new PdfPCell(new Phrase("")));
		IntStream.range(0, cpHeaders.size()).forEach(i -> {
			table.addCell(createDataCell("Value", fontCell));
			table.addCell(createDataCell("% Changes", fontCell));
		});

		// Add data rows
		IntStream.range(0, firstColumnValues.size()).forEach(i -> {
			// Add first column cell for row description
			table.addCell(createDataCell(firstColumnValues.get(i), fontCell));

			// Add data cells for each CP's sub-headers in the row
			rowData.get(i).forEach(data -> table.addCell(createDataCell(data, subCell)));
		});

		document.add(table);
	}

	private static PdfPCell createDataCell(String text, Font font) {
		PdfPCell cell = new PdfPCell(new Phrase(text, font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		return cell;
	}
}
