package com.itext.pdf.generation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringItextPdfGenerationApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringItextPdfGenerationApplication.class, args);

		String input = "Mondy";
		switch (input) {
		case "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" -> System.out.println("Week days");
		case "Saturday", "Sunday" -> System.out.println("Weekends");
		default -> System.out.println("Invalid days");
		}
	}

}
