package com.banking.moneytransferapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class MoneytransferapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoneytransferapiApplication.class, args);
	}

}
