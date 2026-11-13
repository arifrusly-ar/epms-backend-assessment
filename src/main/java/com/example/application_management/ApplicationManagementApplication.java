package com.example.application_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

// Menandakan entry point Spring Boot
@SpringBootApplication
// Mengaktifkan method yang menggunakan @Scheduled
@EnableScheduling
public class ApplicationManagementApplication {

	public static void main(String[] args) {

        SpringApplication.run(ApplicationManagementApplication.class, args);
	}

}
