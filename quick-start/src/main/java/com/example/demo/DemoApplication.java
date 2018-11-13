package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.poscoict.glueframework.biz.control.GlueBizController;
import com.poscoict.glueframework.context.GlueContext;
import com.poscoict.glueframework.context.GlueDefaultContext;


@SpringBootApplication
@ComponentScan( basePackages = { "sample", "com.poscoict.glueframework" } )
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
    @Bean
    public CommandLineRunner run( GlueBizController bizController )
    {
        return ( args -> {
            GlueContext ctx = new GlueDefaultContext( "hello-service" );
            ctx.put( "input", "Glue" );

            bizController.doAction( ctx );

            System.out.println( ctx.get( "result" ) );
        } );
    }
}