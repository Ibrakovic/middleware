package com.middleware.openmrs_openelis_middleware;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableRetry
@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.middleware")
public class OpenmrsOpenelisMiddlewareApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpenmrsOpenelisMiddlewareApplication.class, args);
	}

	@Bean
	CommandLineRunner run() {
		return args -> {
			System.out.println("✅ System läuft. Scheduler übernimmt die Synchronisation.");
		};
	}
}
