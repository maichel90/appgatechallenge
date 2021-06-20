package com.larodriguezm.appgate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AppgateChallengeApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppgateChallengeApplication.class, args);
	}

}
