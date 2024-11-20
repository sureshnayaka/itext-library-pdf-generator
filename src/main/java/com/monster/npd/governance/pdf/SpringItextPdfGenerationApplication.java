package com.monster.npd.governance.pdf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = "com.monster.npd.governance.pdf")
@EntityScan(basePackages = "com.monster.npd.governance.pdf.pojo")

public class SpringItextPdfGenerationApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringItextPdfGenerationApplication.class, args);

	}

}
