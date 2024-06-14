package com.dmadev.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CustomerApplication {
	//TODO сложить в файлы с нужными локалями, например, для локали ru_RU: messages_ru_RU.properties и тд
	// i18

	public static void main(String[] args) {
		SpringApplication.run(CustomerApplication.class, args);
	}

}
