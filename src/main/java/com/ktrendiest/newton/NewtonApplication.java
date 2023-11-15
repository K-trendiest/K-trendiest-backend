package com.ktrendiest.newton;

import com.ktrendiest.newton.domain.Movie;
import com.ktrendiest.newton.service.MovieService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class NewtonApplication {
	public static void main(String[] args) {
		SpringApplication.run(NewtonApplication.class, args);
	}
}
