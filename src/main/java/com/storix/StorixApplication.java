package com.storix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching

public class StorixApplication {

	public static void main(String[] args) {
		SpringApplication.run(StorixApplication.class, args);
	}

}
