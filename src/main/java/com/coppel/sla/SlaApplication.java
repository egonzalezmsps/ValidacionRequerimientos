package com.coppel.sla;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class SlaApplication {

	public static void main(String[] args) {
		System.setProperty("spring.threads.virtual.enabled", "true");
		SpringApplication.run(SlaApplication.class, args);
	}


}
