package com.example.ServerSpring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.example.ServerSpring")
@EnableAsync
public class ServerSpringApplication {
	public static void main(String[] args) {
		SpringApplication.run(ServerSpringApplication.class, args);
	}

}
