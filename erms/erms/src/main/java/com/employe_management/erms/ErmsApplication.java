package com.employe_management.erms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication(scanBasePackages = {"com.employe_management.erms"})

public class ErmsApplication {

	public static void main(String[] args) {

		ApplicationContext context = SpringApplication.run(ErmsApplication.class, args);

		// Retrieve the DataInitializer bean
		DataInitializer dataInitializer = context.getBean(DataInitializer.class);

		// Call the initialization method
		dataInitializer.initializeData();
	}


}
