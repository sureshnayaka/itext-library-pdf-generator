package com.monster.npd.governance.pdf.service;

import org.springframework.http.HttpHeaders;

public interface PdfGeneratorInterface {

	public byte[] generatePdf(long requestId, HttpHeaders headers);

}
