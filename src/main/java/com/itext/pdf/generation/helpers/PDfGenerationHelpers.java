package com.itext.pdf.generation.helpers;

import java.net.URL;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class PDfGenerationHelpers extends PdfPageEventHelper {

	private URL url;

	public PDfGenerationHelpers(URL url) {
		this.url = url;
	}

	@Override
	public void onEndPage(PdfWriter writer, Document document) {
		try {

			ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER,
					new Phrase("Generated on: " + java.time.LocalDate.now(),
							new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC)),
					(document.left() + document.right()) / 2, document.bottom() - 20, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onStartPage(PdfWriter writer, Document document) {

		try {
			Image logo = Image.getInstance(this.url);
			logo.scaleToFit(100, 50);
			logo.setAbsolutePosition(document.left() - 5, document.top() - 15);
			document.add(logo);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
