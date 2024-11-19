package com.monster.npd.governance.pdf.helpers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.PdfPTable;

public class PageHeader {

	private static final String JSON_STRING = "{\n" + "    \"projectDetails\": [\n" + "        {\n"
			+ "            \"label\": \"Project Name\",\n" + "            \"value\": \"Cyprus-Fury\"\n" + "        },\n"
			+ "        {\n" + "            \"label\": \"Project Type\",\n" + "            \"value\": \"Pet\"\n"
			+ "        },\n" + "        {\n" + "            \"label\": \"Project Sub-type\",\n"
			+ "            \"value\": \"Sub type\"\n" + "        },\n" + "        {\n"
			+ "            \"label\": \"Program Tag\",\n" + "            \"value\": \"Program tag\"\n" + "        },\n"
			+ "        {\n" + "            \"label\": \"Reporting Quarter\",\n" + "            \"value\": \"Q1\"\n"
			+ "        },\n" + "        {\n" + "            \"label\": \"Currently Active Stage\",\n"
			+ "            \"value\": \"CP - 0/1/2/3\"\n" + "        },\n" + "        {\n"
			+ "            \"label\": \"EZE PM\",\n" + "            \"value\": \"Jaymal\"\n" + "        },\n"
			+ "        {\n" + "            \"label\": \"Last CP Date\",\n" + "            \"value\": \"12/10/2024\"\n"
			+ "        }\n" + "    ]\n" + "}\n" + "";

	public static void addPageHeaders(Document document) throws DocumentException {
		PDfGenerationHelpers.addTitle(document, "Governance PDF Summary", false);
		float[] columnWidths = { 2f, 3f };
		PdfPTable table = new PdfPTable(columnWidths);
		table.setWidthPercentage(50);
		table.setHorizontalAlignment(Element.ALIGN_CENTER);
		// Parse JSON
		JsonObject jsonObject = JsonParser.parseString(JSON_STRING).getAsJsonObject();
		JsonArray projectDetails = jsonObject.getAsJsonArray("projectDetails");
		Font labelFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, Font.BOLD);
		Font valueFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 9);
		// Add rows from JSON data
		for (JsonElement element : projectDetails) {
			JsonObject detail = element.getAsJsonObject();
			String label = detail.get("label").getAsString();
			String value = detail.get("value").getAsString();

			PDfGenerationHelpers.addTableRow(table, label, value, labelFont, valueFont, Element.ALIGN_CENTER, 2,
					BaseColor.LIGHT_GRAY);
		}
		document.add(table);
	}

}
