package com.poscoict.cateringpass;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jms.annotation.EnableJms;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@SpringBootApplication
@EnableJms
@ComponentScan(basePackages = { "com.poscoict.cateringpass", "com.poscoict.glueframework" })
public class OrderApplication {
	public static void main(String[] args) {
		SpringApplication.run(OrderApplication.class, args);
	}

	@Bean
	public Docket api() {
		// RequestHandlerSelectors.any()
		// RequestHandlerSelectors.basePackage("com.poscoict.cateringpass")
		// RequestHandlerSelectors.basePackage(getClass().getPackage().getName())
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage(getClass().getPackage().getName())) // "com.poscoict.cateringpass"
				.paths(PathSelectors.any()).build();
	}
}
