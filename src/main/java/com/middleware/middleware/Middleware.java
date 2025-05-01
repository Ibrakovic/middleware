package com.middleware.middleware;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableRetry
@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.middleware")
public class Middleware {

	public static void main(String[] args) {
		SpringApplication.run(Middleware.class, args);
	}

	@Bean
	CommandLineRunner run() {
		return args -> {
			System.out.println("System läuft. Scheduler übernimmt die Synchronisation.");
		};
	}
}
