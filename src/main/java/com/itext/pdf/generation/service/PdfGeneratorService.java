package com.itext.pdf.generation.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.itext.pdf.generation.helpers.PDfGenerationHelpers;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class PdfGeneratorService {

	private static final Font TITLE_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.NORMAL);
	private static final Font RED_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.RED);
	private static final Font DEFAULT_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
	private static final URL LOGO_URL;

	static {
	    URL tempUrl = null;
	    try {
	        tempUrl = new URL("https://s-cloudfront.cdn.ap.panopto.com/sessions/_branding/823ca9be-cf06-4f09-9182-acf60045ea8e/637543669628126231_largelogo.png");
	    } catch (MalformedURLException e) {
	        e.printStackTrace(); // Handle exception as needed
	    }
	    LOGO_URL = tempUrl; // Assign the initialized URL
	}




	public byte[] generatePdf() {
		Rectangle pageSize = new Rectangle(450, 350);
		Document document = new Document(pageSize);
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {
			
			PdfWriter writer = PdfWriter.getInstance(document, out);
			writer.setPageEvent(new PDfGenerationHelpers(LOGO_URL));

			document.open();
			addTitle(document, "Student Information");
			addTable(document);
			addRepeatedPages(document, "Student Information");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			document.close();
		}

		return out.toByteArray();
	}

	private void addTitle(Document document, String titleText) throws Exception {
		Paragraph title = new Paragraph(titleText, TITLE_FONT);
		title.setAlignment(Element.ALIGN_CENTER);
		title.setSpacingBefore(50);
		document.add(title);
	}

	private void addImage(Document document, URL url) throws DocumentException, MalformedURLException, IOException {

		Image logo = Image.getInstance(url);
		logo.scaleToFit(250, 250);
		logo.setAlignment(Element.ALIGN_CENTER);
		document.add(logo);
	}

	private void addTable(Document document) throws Exception {
		PdfPTable table = new PdfPTable(3);
		table.setWidthPercentage(100);
		table.setSpacingBefore(50f);

		addTableHeader(table);
		addTableRows(table);

		document.add(table);
	}

	private void addTableHeader(PdfPTable table) {
		Stream.of("ID", "Name", "Class").forEach(columnTitle -> {
			PdfPCell header = new PdfPCell(new Phrase(columnTitle, DEFAULT_FONT));
			header.setBackgroundColor(BaseColor.YELLOW);
			header.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(header);
		});
	}

	private void addTableRows(PdfPTable table) {
		for (int i = 0; i <= 3; i++) {
			Font font = (i % 2 == 0) ? DEFAULT_FONT : RED_FONT;
			BaseColor bgColor = (i % 2 == 0) ? BaseColor.WHITE : BaseColor.DARK_GRAY;

			addTableCell(table, String.valueOf(i), font, bgColor);
			addTableCell(table, "Name " + i, font, bgColor);
			addTableCell(table, "Class " + i, font, bgColor);
		}
	}

	private void addTableCell(PdfPTable table, String text, Font font, BaseColor bgColor) {
		PdfPCell cell = new PdfPCell(new Phrase(text, font));
		cell.setBackgroundColor(bgColor);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
	}

	private void addRepeatedPages(Document document, String titleText) throws Exception {
		for (int i = 0; i < 2; i++) {
			document.newPage();
			addTitle(document, titleText);
			addTable(document);
			if(i==1) {
				addImage(document, LOGO_URL);
			}
		}
	}
}
