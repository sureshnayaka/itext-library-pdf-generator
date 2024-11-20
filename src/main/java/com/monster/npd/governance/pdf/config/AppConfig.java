package com.monster.npd.governance.pdf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.monster.npd.governance.pdf.service.PdfGeneratorInterface;
import com.monster.npd.governance.pdf.service.impl.PdfGeneratorServiceImplementation;


@Configuration
public class AppConfig {

	@Bean
	CustomPhysicalNamingStrategy customPhysicalNamingStrategy() {
		return new CustomPhysicalNamingStrategy();
	}

	@Bean
	PdfGeneratorInterface pdfGeneratorInterface() {
		return new PdfGeneratorServiceImplementation();
	}

}
