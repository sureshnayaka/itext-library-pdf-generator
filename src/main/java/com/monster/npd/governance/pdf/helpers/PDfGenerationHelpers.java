package com.monster.npd.governance.pdf.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.monster.npd.governance.pdf.service.PdfGeneratorService;

/**
 * @author Suresh this is the helper class used to create the pdf table, cells,
 *         add titles etc.
 */
@Component
public class PDfGenerationHelpers extends PdfPageEventHelper {
	private static final Logger logger = LogManager.getLogger(PdfGeneratorService.class);

	private static final Font TITLE_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK);
	private static final Font TITLE_FONT_ACTIVE = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD,
			new BaseColor(38, 86, 66));

	@Override
	public void onStartPage(PdfWriter writer, Document document) {
		try {
			Image logo = loadImage("/assets/header.png");
			logo.scaleToFit(40, 30);
			float x = document.left() - 20;
			float y = document.top() - 10;
			logo.setAbsolutePosition(x, y);
			// writer.getDirectContent().addImage(logo);

			setHeader(writer);
			setBackground(writer);

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
		}

	}

	public void setBackground(PdfWriter writer) throws DocumentException {
		Image backgroundImage = loadImage("/assets/Back.jpg");

		backgroundImage.scaleAbsolute(PageSize.A4.getHeight(), PageSize.A4.getWidth());

		PdfGState gState = new PdfGState();
		gState.setFillOpacity(0.1f); // Set opacity to 50%

		PdfContentByte canvas = writer.getDirectContentUnder();
		canvas.setGState(gState);
		backgroundImage.setAbsolutePosition(0, 0);
		canvas.addImage(backgroundImage);
	}

	public void setHeader(PdfWriter writer) throws DocumentException {
		Image headerImage = loadImage("/assets/backHeader.png");

		float headerHeight = 45f;

		headerImage.scaleAbsolute(PageSize.A4.getHeight(), headerHeight);

		headerImage.setAbsolutePosition(0, PageSize.A4.getWidth() - headerHeight);

		PdfContentByte canvas = writer.getDirectContentUnder();
		canvas.addImage(headerImage);
	}

	@Override
	public void onEndPage(PdfWriter writer, Document document) {
		try {
			// background image
			// PdfContentByte canvas = writer.getDirectContentUnder();

			// canvas.addImage(backgroundImage);
//	
			// background color

//			canvas.setColorFill(new BaseColor(233, 233, 233));
//
//			canvas.rectangle(0, 0, document.getPageSize().getWidth(), document.getPageSize().getHeight());
//			canvas.fill();

			// footer copy right text
//			ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER,
//					new Phrase("Copyright © 2024 Acheron Software Consultany Pvt. Ltd. All Rights Reserved.",
//							new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.BLACK)),
//					(document.left() + document.right() / 2), document.bottom() - 20, 0);

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
		}
	}

	/**
	 * To load the image from the provided path
	 * 
	 */
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
	 * To add table names on the tables
	 */
	public static void addHeaderTitles(Document document, String HeaderTitle) throws DocumentException {
		Paragraph title = new Paragraph(HeaderTitle, new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.BOLD));
		title.setAlignment(Element.ALIGN_LEFT);
		title.setFirstLineIndent(5f);
		title.setSpacingAfter(5f);
		document.add(title);
	}

	/**
	 * @Description - header title for pages
	 * @param document
	 * @param titleText
	 * @throws Exception
	 */
	public static void addTitle(Document document, String titleText, boolean isActive) {
		try {
			Paragraph title = new Paragraph(titleText, isActive ? TITLE_FONT_ACTIVE : TITLE_FONT);
			title.setAlignment(Element.ALIGN_CENTER);
			title.setSpacingAfter(5f);
			document.add(title);
		} catch (DocumentException exception) {
			logger.error(exception.getMessage());
		}
	}

	/**
	 * To add row/Cells in the table
	 */
	public static void addRow(PdfPTable table, String... values) throws DocumentException {
		Font cellFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK); // White content font
		for (String value : values) {
			PdfPCell cell = new PdfPCell(new Phrase(value, cellFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setPadding(5);
			cell.setNoWrap(false); // Prevent text wrapping
			table.addCell(cell);
		}
	}

	/**
	 * To add the paragraph like comments/ messages
	 */
	public static void addChunkParagraph(Document document, String key, String value) throws DocumentException {
		Font boldFont = FontFactory.getFont(FontFactory.TIMES_BOLD, 9);
		Font normalFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 9);

		Phrase phrase = new Phrase();
		phrase.add(new Chunk(key, boldFont));
		phrase.add(new Chunk(value, normalFont));

		PdfPCell cell = new PdfPCell(phrase);
		cell.setBorder(Rectangle.NO_BORDER); // Remove border
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);

		PdfPTable table = new PdfPTable(1);
		table.setWidthPercentage(99);
		table.addCell(cell);
		table.setSpacingBefore(2f);
		document.add(table);
	}

	public static void addTableRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont,
			int alignment, float padding, BaseColor borderColor) {
		PdfPCell labelCell = createStyledCell(label, labelFont, alignment, padding, borderColor);
		PdfPCell valueCell = createStyledCell(value, valueFont, alignment, padding, borderColor);
		table.addCell(labelCell);
		table.addCell(valueCell);
	}

	private static PdfPCell createStyledCell(String content, Font font, int alignment, float padding,
			BaseColor borderColor) {
		PdfPCell cell = new PdfPCell(new Phrase(content, font));
		cell.setPadding(padding);
		cell.setHorizontalAlignment(alignment);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setNoWrap(false);
		cell.setBorderColor(borderColor);
		return cell;
	}

	public static void setTableHeader(PdfPTable table, String... headers) {
		Font headerFont = FontFactory.getFont(FontFactory.TIMES_BOLD, 9, BaseColor.WHITE);
		IntStream.range(0, headers.length).forEach(i -> {
			PdfPCell cell = new PdfPCell(new Phrase(headers[i], headerFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setPadding(5);
			cell.setNoWrap(false);
			cell.setBackgroundColor(BaseColor.BLACK);
			cell.setBorderColor(BaseColor.WHITE);
			if (i == 0) {
				cell.setBorderColorLeft(BaseColor.BLACK);
				cell.setBorderWidthLeft(10f);

			} else if (i == headers.length - 1) {
				cell.setBorderColorRight(BaseColor.BLACK);
				cell.setBorderWidthRight(10f);
			}

			table.addCell(cell);
		});
	}

	public static void addTableData(Document document, String tableName, float[] columnWidths, String[][] rowsValues,
			String[] headers) {
		try {

			PdfPTable table = new PdfPTable(columnWidths);
			table.setWidthPercentage(99);
			table.setHorizontalAlignment(Element.ALIGN_CENTER);
			addHeaderTitles(document, tableName);

			setTableHeader(table, headers);
			for (String[] row : rowsValues) {
				addRow(table, row);
			}
			document.add(table);
		} catch (DocumentException e) {
			logger.error(e.getMessage());
		}

	}

}
