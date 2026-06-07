package com.loantrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LoantrackApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoantrackApplication.class, args);
		
	}

}
