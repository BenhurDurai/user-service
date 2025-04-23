package com.poc.UserMicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.poc.UserMicroservice", "com.poc.commonSecurities"})
public class UserMicroservice {

	public static void main(String[] args) {
		SpringApplication.run(UserMicroservice.class, args);
	}

}
