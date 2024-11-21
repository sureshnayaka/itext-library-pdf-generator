package com.monster.npd.governance.pdf.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfPTable;

@Component
public class GovernanceAuditSummaryHelper {

	@Autowired
	CpDataProcessor cpDataProcessor;

	public void addGovernanceAuditSummary(Document document, long requestId) throws DocumentException {

		float[] columnWidths = { 3f, 2f, 3.5f, 3.5f };
		PdfPTable table = new PdfPTable(columnWidths);
		table.setWidthPercentage(99);
		table.setHorizontalAlignment(Element.ALIGN_CENTER);
		String[] headers = { "Name", "Action", "Comments", "Performed Date" };

		PDfGenerationHelpers.setTableHeader(table, headers);
		long longValue = requestId;
		int intValue = (int) longValue;

		cpDataProcessor.getGovernanceAuditSummary(intValue).forEach(action -> {
			try {
				PDfGenerationHelpers.addRow(table, action.getName(), action.getAction(), action.getComments(),
						String.valueOf(action.getDate()));
			} catch (DocumentException e) {
				e.printStackTrace();
			}
		});
		document.add(table);
	}
}
