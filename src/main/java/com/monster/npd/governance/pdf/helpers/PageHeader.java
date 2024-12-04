package com.monster.npd.governance.pdf.helpers;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import com.monster.npd.governance.pdf.utils.Utils;

@Component
public class PageHeader {

	private static final Logger LOGGER = LogManager.getLogger(PageHeader.class);

	private static final float HEADER_FONT_SIZE = 12f;
	private static final float LINE_HEIGHT = 14f;
	private static final float MARGIN_OFFSET = 100f;
	private static final float INITIAL_Y_OFFSET = 90f;
	private static final String FONT_TYPE = BaseFont.TIMES_BOLD;
	private static final String FONT_ENCODING = BaseFont.CP1252;
	private static final boolean FONT_EMBEDDED = BaseFont.NOT_EMBEDDED;
	private final CpDataProcessor cpDataProcessor;

	public PageHeader(CpDataProcessor cpDataProcessor) {
		this.cpDataProcessor = cpDataProcessor;
	}

	/**
	 * Sets the main heading of the PDF page.
	 *
	 * @param writer    the PDF writer
	 * @param document  the PDF document
	 * @param requestId the request ID
	 * @return the heading text
	 */
	public String setMainHeading(PdfWriter writer, Document document, Long requestId) {
		String heading = "";
		try {
			heading = cpDataProcessor.getHeaderValues(requestId);
			if (Utils.isNullOrEmptyString(heading)) {
				LOGGER.warn("Heading is null or empty for requestId: {}", requestId);
				return "";
			}

			drawHeading(writer, document, heading);
		} catch (Exception e) {
			LOGGER.error("Error while setting main heading: {}", e.getMessage(), e);
		}
		return heading;
	}

	/**
	 * Draws the heading on the PDF document.
	 *
	 * @param writer   the PDF writer
	 * @param document the PDF document
	 * @param heading  the heading text
	 * @throws IOException 
	 * @throws DocumentException 
	 */
	private void drawHeading(PdfWriter writer, Document document, String heading) throws DocumentException, IOException {
		float x = document.left() + 20;
		float y = document.top() - INITIAL_Y_OFFSET;
		PdfContentByte canvas = writer.getDirectContent();
		BaseFont baseFont = BaseFont.createFont(FONT_TYPE, FONT_ENCODING, FONT_EMBEDDED);

		canvas.beginText();
		canvas.setFontAndSize(baseFont, HEADER_FONT_SIZE);
		canvas.setColorFill(Color.WHITE);

		float maxWidth = document.right() - document.left() - MARGIN_OFFSET;
		float currentY = y + 100;

		for (String line : wrapText(heading, baseFont, HEADER_FONT_SIZE, maxWidth)) {
			canvas.showTextAligned(Element.ALIGN_LEFT, line, x, currentY, 0);
			currentY -= LINE_HEIGHT;
		}

		canvas.endText();
	}

	/**
	 * Wraps text into multiple lines based on the maximum width.
	 *
	 * @param text     the text to wrap
	 * @param font     the font used for the text
	 * @param fontSize the size of the font
	 * @param maxWidth the maximum width for a single line
	 * @return a list of wrapped text lines
	 */
	private List<String> wrapText(String text, BaseFont font, float fontSize, float maxWidth) {
		List<String> lines = new ArrayList<>();
		StringBuilder currentLine = new StringBuilder();
		float currentWidth = 0;

		for (String word : text.split(" ")) {
			float wordWidth = font.getWidthPoint(word + " ", fontSize);
			if (currentWidth + wordWidth > maxWidth) {
				lines.add(currentLine.toString().trim());
				currentLine = new StringBuilder(word).append(" ");
				currentWidth = wordWidth;
			} else {
				currentLine.append(word).append(" ");
				currentWidth += wordWidth;
			}
		}

		if (!currentLine.toString().trim().isEmpty()) {
			lines.add(currentLine.toString().trim());
		}

		return lines;
	}
}
