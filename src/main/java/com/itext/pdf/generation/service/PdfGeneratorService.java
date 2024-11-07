package com.itext.pdf.generation.service;

import java.io.ByteArrayOutputStream;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.itext.pdf.generation.helpers.PDfGenerationHelpers;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.net.URL;

@Service
public class PdfGeneratorService {

	public byte[] generatePdf() {
		Rectangle rectangle3x5 = new Rectangle(450, 350);
		Document document = new Document(rectangle3x5);
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {
			PdfWriter writer = PdfWriter.getInstance(document, out);

			// Set the custom page event for header and footer
			PDfGenerationHelpers event = new PDfGenerationHelpers(new URL(
					"https://s-cloudfront.cdn.ap.panopto.com/sessions/_branding/823ca9be-cf06-4f09-9182-acf60045ea8e/637543669628126231_largelogo.png"));
			writer.setPageEvent(event);

			document.open();

			// Title
			Paragraph title = new Paragraph("Student Information", new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD));
			title.setAlignment(Element.ALIGN_CENTER);
			title.setSpacingBefore(50); // Add spacing to avoid overlap with header
			document.add(title);

			// Table
			PdfPTable table = new PdfPTable(3);
			table.setWidthPercentage(100);
			table.setSpacingBefore(50f);

			Stream.of("ID", "Name", "Class").forEach(columnTitle -> {
				PdfPCell header = new PdfPCell();
				header.setBackgroundColor(BaseColor.YELLOW);
				header.setBorderWidth(1);
				header.setHorizontalAlignment(Element.ALIGN_CENTER); // Align center
				header.setPhrase(new Phrase(columnTitle));
				table.addCell(header);
			});

			// Sample Data
			for (int i = 0; i <= 3; i++) {
				if (i % 2 == 0) {
					PdfPCell idCell = new PdfPCell(new Phrase(String.valueOf(i)));
					idCell.setBackgroundColor(BaseColor.WHITE);
					idCell.setHorizontalAlignment(Element.ALIGN_CENTER); // Align center
					table.addCell(idCell);

					// Name cell
					PdfPCell nameCell = new PdfPCell(new Phrase("Name " + i));
					nameCell.setBackgroundColor(BaseColor.WHITE);
					nameCell.setHorizontalAlignment(Element.ALIGN_CENTER); // Align center
					table.addCell(nameCell);

					// Class cell
					PdfPCell classCell = new PdfPCell(new Phrase("Class " + i));
					classCell.setBackgroundColor(BaseColor.WHITE);
					classCell.setHorizontalAlignment(Element.ALIGN_CENTER); // Align center
					table.addCell(classCell);
				} else {
					// Define the font with red color
					Font redFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.RED);

					// ID cell with alignment and color
					PdfPCell idCell = new PdfPCell(new Phrase(String.valueOf(i), redFont));
					idCell.setBackgroundColor(BaseColor.DARK_GRAY);
					idCell.setHorizontalAlignment(Element.ALIGN_CENTER); // Align center
					table.addCell(idCell);

					// Name cell with alignment and color
					PdfPCell nameCell = new PdfPCell(new Phrase("Name " + i, redFont));
					nameCell.setBackgroundColor(BaseColor.DARK_GRAY);
					nameCell.setHorizontalAlignment(Element.ALIGN_CENTER); // Align center
					table.addCell(nameCell);

					// Class cell with alignment and color
					PdfPCell classCell = new PdfPCell(new Phrase("Class " + i, redFont));
					classCell.setBackgroundColor(BaseColor.DARK_GRAY);
					classCell.setHorizontalAlignment(Element.ALIGN_CENTER); // Align center
					table.addCell(classCell);

				}
			}
			document.add(table);
			document.newPage();
			Paragraph page2Text = new Paragraph("This is page 2",
					new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.BLUE));
			page2Text.setSpacingBefore(50f);
			page2Text.setAlignment(Element.ALIGN_CENTER);
			page2Text.setExtraParagraphSpace(10f);
			document.add(page2Text);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			document.close();
		}

		return out.toByteArray();
	}
}