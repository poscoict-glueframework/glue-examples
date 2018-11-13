package com.poscoict.cateringpass;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@ComponentScan(basePackages = { "com.poscoict.cateringpass", "com.poscoict.glueframework" })
@EnableSwagger2
public class CateringPassApplication {
	public static void main(String[] args) {
		SpringApplication.run(CateringPassApplication.class, args);
	}

	@Bean
	public Docket api() {
		// RequestHandlerSelectors.any()
		// RequestHandlerSelectors.basePackage("com.poscoict.cateringpass")
		// RequestHandlerSelectors.basePackage(getClass().getPackage().getName())
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage(getClass().getPackage().getName())) //"com.poscoict.cateringpass"
				.paths(PathSelectors.any())
				.build();
	}
}
