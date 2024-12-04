package com.monster.npd.governance.pdf;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import com.monster.npd.governance.pdf.utils.Utils;

public class GeneratePDFWithoutBleed {
	public Image loadImage(String filePath) {
		try (InputStream input = getClass().getResourceAsStream(filePath)) {
			if (!Utils.isNullOrEmptyObject(input)) {
				return Image.getInstance(input.readAllBytes());
			}
		} catch (IOException | BadElementException e) {
		}
		return null;
	}
    public  void pdf() {
        try {
            // Output PDF file
            String outputFile = "a4_no_bleed.pdf";

            // Create a document with A4 size in landscape orientation
            Document document = new Document(PageSize.A4.rotate(), 36, 36, 36, 36); // Margins: 36 points (0.5 inch)

            // Create a PdfWriter instance
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputFile));

            // Open the document
            document.open();

            // Add a background image
            Image backgroundImage = loadImage("/assets/Back.jpg");
            backgroundImage.scaleAbsolute(PageSize.A4.getHeight(), PageSize.A4.getWidth());
            backgroundImage.setAbsolutePosition(0, 0);

            PdfContentByte canvas = writer.getDirectContentUnder();
            canvas.addImage(backgroundImage);

            // Add a header image
            Image headerImage = loadImage("/assets/headerM.png");
            float headerHeight = 50f; // Height of the header
            headerImage.scaleAbsolute(PageSize.A4.getHeight(), headerHeight);
            headerImage.setAbsolutePosition(0, PageSize.A4.getWidth() - headerHeight);
            canvas.addImage(headerImage);

            // Add some content
            Font font = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);
            Paragraph content = new Paragraph("This is a sample PDF generated without bleed.", font);
            document.add(content);

            // Close the document
            document.close();

            System.out.println("PDF generated without bleed successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
   
}
