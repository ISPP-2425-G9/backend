package com.caronte.caronte;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class CaronteApplication {

	public static void main(String[] args) {
		SpringApplication.run(CaronteApplication.class, args);
	}

}
