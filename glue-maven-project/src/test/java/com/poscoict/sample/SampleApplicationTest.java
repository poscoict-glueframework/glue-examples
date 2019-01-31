package com.poscoict.sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.poscoict.sample.jpa.EmpJpo;
import com.poscoict.sample.jpa.EmpRepository;

@SpringBootApplication
public class SampleApplicationTest {
	@Autowired
	ApplicationContext applicationContext;

	public static void main(String[] args) {
		System.out.println("### SampleApplicationTest.main() ###");
		SpringApplication.run(SampleApplicationTest.class, args);
		System.out.println("### SpringApplication.run ###");
	}

	@Bean
	public CommandLineRunner init(EmpRepository repository) {
		return (args -> {
			System.out.println("### CommandLineRunner init() start ###");
			EmpJpo jpo = new EmpJpo();
			jpo.setEmpno(1111);
			jpo.setEname("yujin");
			repository.save(jpo);
			System.out.println("### CommandLineRunner init() end   ###");

			for (String name : applicationContext.getBeanDefinitionNames()) {
				if (applicationContext.getBean(name).getClass().getName().startsWith("com.poscoict")) {
					System.out.println(name + "\t:" + applicationContext.getBean(name));
				}
			}
			System.out.println(" \t" + ":" + applicationContext.getBean(EmpRepository.class));
		});
	}
}
