package com.poscoict.cateringpass;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CateringPassApplication {

	public static void main(String[] args) {
		SpringApplication.run(CateringPassApplication.class, args);
	}

	@Bean
	public CommandLineRunner run(Firebase fb) {
		return (args -> {
			fb.doingSomething();
		});
	}
}
