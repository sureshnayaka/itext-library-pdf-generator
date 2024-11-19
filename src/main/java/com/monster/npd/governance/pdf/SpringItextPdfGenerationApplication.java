package com.monster.npd.governance.pdf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = "com.monster.npd.governance.pdf")
public class SpringItextPdfGenerationApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringItextPdfGenerationApplication.class, args);

	}

}
