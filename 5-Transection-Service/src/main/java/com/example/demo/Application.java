package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.example.demo.client")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		 System.out.println("---- TRANSACTION SERVICE STARTED ----");
	}

}
