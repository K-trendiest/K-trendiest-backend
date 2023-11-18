package com.ktrendiest.newton;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class NewtonApplication {
	public static void main(String[] args) {
		SpringApplication.run(NewtonApplication.class, args);
	}
}
