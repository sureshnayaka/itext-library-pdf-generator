package com.monster.npd.governance.pdf.helpers;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import com.monster.npd.governance.pdf.utils.Utils;

/**
 * @author Suresh this is the helper class used to create the pdf table, cells,
 *         add titles etc.
 */
@Component
public class PDfGenerationHelpers extends PdfPageEventHelper {
	private static final Logger logger = LogManager.getLogger(PDfGenerationHelpers.class);

	private static final Font TITLE_FONT = new Font(
			FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.BOLD, Color.BLACK));
	private static final Font TITLE_FONT_ACTIVE = new Font(
			FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.BOLD, new Color(38, 86, 66)));
	private static final String HEADER_PATH ="/assets/headerM.png";
	private static final String BACKGROUND_PATH ="/assets/Back.jpg";

	@Override
	public void onStartPage(PdfWriter writer, Document document) {
		try {
			setHeader(writer, document);
			setBackground(writer);

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
		}
	}

	public void setBackground(PdfWriter writer) throws DocumentException {
		Image backgroundImage = loadImage(BACKGROUND_PATH);
		backgroundImage.scaleAbsolute(PageSize.A4.getHeight(), PageSize.A4.getWidth());
		backgroundImage.setAbsolutePosition(0, 0);

		PdfContentByte canvas = writer.getDirectContentUnder();
		PdfGState gState = new PdfGState();
		gState.setFillOpacity(0.1f); // Set opacity to 50%
		canvas.setGState(gState);
		canvas.addImage(backgroundImage);

	}

	public void setHeader(PdfWriter writer, Document document) throws DocumentException {
		Image headerImage = loadImage(HEADER_PATH);

		float headerHeight = 40f;
		PdfGState gState = new PdfGState();
		gState.setFillOpacity(0.9f); // Set opacity to 50%

		headerImage.scaleAbsolute(PageSize.A4.getHeight(), headerHeight);

		headerImage.setAbsolutePosition(0, PageSize.A4.getWidth() - headerHeight);

		PdfContentByte canvas = writer.getDirectContentUnder();
		canvas.setGState(gState);
		canvas.addImage(headerImage);
		addSpacer(document);
	}

	public void addSpacer(Document document) {

		try {
			Paragraph title = new Paragraph(" ", new Font(Font.TIMES_ROMAN, 9, Font.BOLD));
			title.setSpacingBefore(8f);
			document.add(title);
		} catch (DocumentException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onEndPage(PdfWriter writer, Document document) {
		try {
			
			ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER,
					new Phrase(String.valueOf(document.getPageNumber()),
							new Font(Font.TIMES_ROMAN, 12, Font.NORMAL, Color.BLACK)),
					(document.left()), document.bottom() - 20, 0);

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
			if (!Utils.isNullOrEmptyObject(input)) {
				return Image.getInstance(input.readAllBytes());
			}
		} catch (IOException | BadElementException e) {
			logger.error("Error loading image: " + e.getMessage());
		}
		return null;
	}

	/**
	 * To add table names on the tables
	 */
	public static void addHeaderTitles(Document document, String HeaderTitle) throws DocumentException {
		Paragraph title = new Paragraph(HeaderTitle, new Font(Font.TIMES_ROMAN, 9, Font.BOLD));
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
		Font cellFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, Color.BLACK); // White content font
		for (String value : values) {
			PdfPCell cell = new PdfPCell(new Phrase(value, cellFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setPadding(5);
			cell.setNoWrap(false); // Prevent text wrapping
			table.addCell(cell);
		}
	}

	public static void addRowSummary(PdfPTable table, String... values) throws DocumentException {
		// Default font for cell content
		Font cellFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, Color.BLACK);
		Font fontStyle_30 = FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, Color.RED);
		for (int i = 0; i < values.length; i++) {
			PdfPCell cell = new PdfPCell(new Phrase(values[i], cellFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setPadding(5);
			cell.setNoWrap(false);

			if (i == 2) {
				try {
					String volumeString = values[i].replace("%", "").trim(); // Remove % symbol
					float volumePercentage = Float.parseFloat(volumeString);

					if (volumePercentage > 30) {
						cell.setPhrase(new Phrase(values[i], fontStyle_30));
					}
				} catch (NumberFormatException e) {
					logger.error("Invalid percentage format: " + values[i]);
				}
			}
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
		table.setWidthPercentage(100);
		table.addCell(cell);
		table.setSpacingBefore(2f);
		document.add(table);
	}

	public static void addTableRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont,
			int alignment, float padding, Color borderColor) {
		PdfPCell labelCell = createStyledCell(label, labelFont, alignment, padding, borderColor);
		PdfPCell valueCell = createStyledCell(value, valueFont, alignment, padding, borderColor);
		table.addCell(labelCell);
		table.addCell(valueCell);
	}

	private static PdfPCell createStyledCell(String content, Font font, int alignment, float padding,
			Color borderColor) {
		PdfPCell cell = new PdfPCell(new Phrase(content, font));
		cell.setPadding(padding);
		cell.setHorizontalAlignment(alignment);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setNoWrap(false);
		cell.setBorderColor(borderColor);
		return cell;
	}

	public static void setTableHeader(PdfPTable table, String tableTitle, String... headers) {
		Font headerFont = FontFactory.getFont(FontFactory.TIMES_BOLD, 9, Color.WHITE);
		Font smallerFont = FontFactory.getFont(FontFactory.TIMES_BOLD, 7, Color.WHITE);

		IntStream.range(0, headers.length).forEach(i -> {
			PdfPCell cell = new PdfPCell();
			String headerValue = headers[i];
			if (headerValue.contains("EUROS")) {
				headerValue = headerValue.replaceAll("(?i)EUROS", "€ ");
			}

			if (headerValue.contains("(") && headerValue.contains(")")) {
				String mainText = headerValue.substring(0, headerValue.indexOf("(")).trim();
				String parenthesesText = headerValue.substring(headerValue.indexOf("("));

				Chunk mainChunk = new Chunk(mainText + " ", headerFont); // Main text
				Chunk parenthesesChunk = new Chunk(parenthesesText, smallerFont); // Text in parentheses

				Phrase phrase = new Phrase();
				phrase.add(mainChunk);
				phrase.add(parenthesesChunk);
				cell = new PdfPCell(phrase);
			} else {
				cell = new PdfPCell(new Phrase(headerValue, headerFont));
			}

			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setPadding(5);
			cell.setNoWrap(false);
			cell.setBackgroundColor(Color.BLACK);
			cell.setBorderColor(Color.WHITE);
			if (i == 0) {
				cell.setBorderColorLeft(Color.BLACK);
				cell.setBorderWidthLeft(20f);

			} else if (i == headers.length - 1) {
				cell.setBorderColorRight(Color.BLACK);
				cell.setBorderWidthRight(20f);
			}
			table.addCell(cell);
		});
	}

	public static void addTableData(Document document, String tableName, float[] columnWidths, String[][] rowsValues,
			String[] headers, boolean isSummary) {
		try {

			PdfPTable table = new PdfPTable(columnWidths);
			table.setWidthPercentage(100);
			table.setHorizontalAlignment(Element.ALIGN_CENTER);
			addHeaderTitles(document, tableName);

			setTableHeader(table, tableName, headers);
			for (String[] row : rowsValues) {
				if (isSummary) {
					addRowSummary(table, row); // Use summary style
				} else {
					addRow(table, row); // Use default row style
				}
			}
			document.add(table);
		} catch (DocumentException e) {
			logger.error(e.getMessage());
		}

	}

	public static void addChunkHeaderCheckPoint(Document document, String value, String approvedDate)
			throws DocumentException {

		Color baseColor = new Color(186, 140, 220);
		CheckpointHandler.CheckpointDetails details = CheckpointHandler.getCheckpointDetails(value);
		value = details.getName(); // Dynamically fetched checkpoint name
		baseColor = details.getBaseColor(); // Dynamically fetched Color
		PdfPTable table = new PdfPTable(3);
		table.setWidthPercentage(100);
		table.setSpacingBefore(4f);
		table.setHorizontalAlignment(Element.ALIGN_CENTER);
		float[] columnWidths = { 4f, 0f, 3f };
		table.setWidths(columnWidths);

		PdfPCell firstCell = new PdfPCell(new Phrase(value, new Font(Font.TIMES_ROMAN, 12, Font.BOLD, Color.BLACK)));
		firstCell.setBackgroundColor(Color.BLACK);
		firstCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		firstCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		firstCell.setPaddingBottom(6);
		firstCell.setFixedHeight(20);
		firstCell.setBackgroundColor(baseColor);
		firstCell.setBorderWidthRight(0f);

		PdfPCell secondCell = new PdfPCell(
				new Phrase(approvedDate, new Font(Font.TIMES_ROMAN, 12, Font.BOLD, Color.BLACK)));
		secondCell.setBorderColor(Color.BLACK);
		secondCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		secondCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		secondCell.setPaddingBottom(6);
		secondCell.setBackgroundColor(baseColor);
		secondCell.setBorderWidthLeft(0f);

		PdfPCell spacerCell = new PdfPCell();
		spacerCell.setBorder(PdfPCell.NO_BORDER);

		// Add cells to table
		table.addCell(firstCell);
		table.addCell(spacerCell);
		table.addCell(secondCell);

		// Add table to document
		document.add(table);
	}

	public static void addChunkComments(Document document, String key, String value, int tableWidth, float cellwdith,
			float spaceWidth) throws DocumentException {

		PdfPTable table = new PdfPTable(3);
		table.setWidthPercentage(tableWidth);
		table.setSpacingBefore(3f);
		table.setHorizontalAlignment(Element.ALIGN_RIGHT);
		float[] columnWidths = { cellwdith, spaceWidth, 5f };
		table.setWidths(columnWidths);

		PdfPCell firstCell = new PdfPCell(new Phrase(key, new Font(Font.TIMES_ROMAN, 9, Font.BOLD, Color.WHITE)));
		firstCell.setBackgroundColor(Color.BLACK);
		firstCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		firstCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		firstCell.setPaddingBottom(6);
		firstCell.setFixedHeight(20);

		PdfPCell secondCell = new PdfPCell(new Phrase(value, new Font(Font.TIMES_ROMAN, 8, Font.NORMAL, Color.BLACK)));
		secondCell.setBorderColor(Color.BLACK);
		secondCell.setBorderWidth(1f);
		secondCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		secondCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		secondCell.setPaddingBottom(5);
		secondCell.setNoWrap(false);

		PdfPCell spacerCell = new PdfPCell();
		spacerCell.setBorder(PdfPCell.NO_BORDER);

		// Add cells to table
		table.addCell(firstCell);
		table.addCell(spacerCell);
		table.addCell(secondCell);

		// Add table to document
		document.add(table);
	}

	public void setCPHeaderTitleOnTopRight(String cpName, Document document, PdfWriter writer) {
		try {
			BaseFont baseFont = BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
			float x = document.right() - 250;
			float y = document.top() - 90;
			PdfContentByte canvas = writer.getDirectContent();
			int fontSize = 12;
			float textWidth = 36f;
			float textHeight = 26f;

			CheckpointHandler.CheckpointDetails details = CheckpointHandler.getCheckpointDetails(cpName);
			Color baseColor = details.getBaseColor();
			canvas.setColorFill(baseColor);
			float rectX = x + 260 - textWidth / 2;
			float rectY = y + 100 - textHeight / 2;
			canvas.rectangle(rectX, rectY, textWidth, textHeight);
			canvas.fill();
			canvas.beginText();
			canvas.setFontAndSize(baseFont, fontSize);
			canvas.setColorFill(Color.BLACK);
			canvas.setLineDash(2);
			canvas.showTextAligned(Element.ALIGN_CENTER, cpName, x + 260, y + 95, 0);
			canvas.endText();
		} catch (Exception exception) {
			logger.error(exception.getMessage());
		}
	}

	public void setCPHeaderImage(String cpName, Document document, PdfWriter writer) {
		try {
			
			float x = document.left();
			float y = document.top() - 82;	
			PdfContentByte canvas = writer.getDirectContent();
			float width = 150f;
			float height = 67.5f;
			canvas.saveState();
			PdfGState gstate = new PdfGState();
			gstate.setFillOpacity(0f);
			gstate.setStrokeOpacity(1f);
			canvas.setGState(gstate);
			canvas.setColorStroke(new Color(0, 102, 0));
			canvas.setLineDash(3f, 3f);
			canvas.setLineWidth(1f);
			canvas.rectangle(x, y, width, height);
			canvas.stroke();

			canvas.restoreState();
		} catch (Exception exception) {
			logger.error(exception.getMessage());
		}
	}

}
