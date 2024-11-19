package com.monster.npd.governance.pdf.helpers;

import org.springframework.stereotype.Component;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;

@Component
public class CP0PageTemplate {


	public void addMarketScopeTableForCp0(Document document) throws DocumentException {

		float[] columnWidths = { 3f, 2f, 3.5f, 3.5f, 4f, 2f, 2f, 4f, 2.5f, 3f, 2f, 2.5f, 3.5f };

		String[][] rowData = {
				{ "Cyprus", "Yes", "1000", "1000000", "12", "0.0", "11", "123", "NO", "€ 123.0", "-10999. 0",
						"Infinity%", "48/2024" },
				{ "Albania", "No", "900", "90000", "12", "0.0", "11", "123", "NO", "€ 0.0", "-99.0", "Infinity%",
						"24/2024" },
				{ "Algeria", "No", "450", "3500", "12", "0.0", "11", "123", "Yes", "€ 0.0", "-45.0", "Infinity%",
						"12/2024" },
				{ "Australia", "No", "350", "2400", "12", "0.0", "11", "124", "No", "€ 0.0", "-35.0", "Infinity%",
						"56/2024" } };

		String[] headers = { "Harmonised Market (S) ", "Lead Market", "3 Month Launch Volume (24 EQ)",
				"Annualised Year 1 Volume (24 EQ)", "Cannabalisation Impact(Total Annual Cases)", "NSV / Case",
				"COG'S / Case", "Annualised Year 1 NSV (Local Currency)", "Currency in Euros?",
				"Annualised Year 1 NSV (EURO)", "Gross Profit", "Gross Margin", "Target DP in Warehouse Week/Year" };

		PDfGenerationHelpers.addTableData(document, "Market-scope", columnWidths, rowData, headers);

	}

	public void addDeliverablesVSThresholdForCp0(Document document) throws DocumentException {

		float[] columnWidths = { 2.5f, 2.5f, 2.5f, 2.5f, 2f, 2f, 2f };

		String[] headers = { " ", "NSV Per Case", "GM %", "Target COG`S", "Total Volume", "Unit ROS (UROS)",
				"Numerical Distribution (ND)" };

		String[][] rowData = { { "This Project", "1000", "1000000", "12", "0.0", " ", "123" },
				{ "% of Monster Green", " ", "900", "90000", " ", " ", "11" } };
		PDfGenerationHelpers.addTableData(document, "Deliverables V/S Thresholds:", columnWidths, rowData, headers);
	}

	public void addPortFolioStartegyForCp0(Document document) throws DocumentException {

		float[] columnWidths = { 3.5f, 3.5f, 4f, 5f, 3f, };

		String[] headers = { "Incremental Or Replacement SKU?", "What Is The Portfolio Delist Strategy?",
				"Specific Cut Off Date For Introduction Of New SKU?", "What is driving your launch date?",
				"Commercial Strategy(Consumer Price Proposal)" };

		String[][] rowData = { { "Incremental", "N/A", "Hard", " ", " " } };
		PDfGenerationHelpers.addTableData(document, "Portfolio Strategy :", columnWidths, rowData, headers);

	}

}
