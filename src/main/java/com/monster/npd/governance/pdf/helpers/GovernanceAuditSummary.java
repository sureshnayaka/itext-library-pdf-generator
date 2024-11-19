package com.monster.npd.governance.pdf.helpers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfPTable;
import com.monster.npd.governance.pdf.pojo.GovernanceAction;

@Component
public class GovernanceAuditSummary {

	public void addGovernanceAuditSummary(Document document) throws DocumentException {

		float[] columnWidths = { 3f, 2f, 3.5f, };
		PdfPTable table = new PdfPTable(columnWidths);
		table.setWidthPercentage(99);
		table.setHorizontalAlignment(Element.ALIGN_CENTER);

		// Define table headers
		String[] headers = { "Name", "Action", "Comments" };

		// Add headers to the table
		PDfGenerationHelpers.setTableHeader(table, headers);

		List<GovernanceAction> governanceActions = new ArrayList<>();
		governanceActions.add(new GovernanceAction("Jhone Doe", "Approved", "No changes vs previous"));
		governanceActions.add(new GovernanceAction("Jaymal", "Rejected", " Remove all data"));
		governanceActions.add(new GovernanceAction("Chris", "Partial approved", "Minor updated needed"));
		governanceActions.add(new GovernanceAction("Henry", "Approved", "No changes vs previous"));

		governanceActions.forEach(action -> {
			try {
			PDfGenerationHelpers.addRow(table, action.getName(), action.getAction(), action.getComments());
			} catch (DocumentException e) {
				e.printStackTrace();
			}
		});
		document.add(table);
	}
}
