package com.pocket.wallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableJpaAuditing
public class WalletApplication {

	public static void main(String[] args) {


		Dotenv dotenv = Dotenv.load();


		dotenv.entries().forEach(entry ->
			System.setProperty(entry.getKey(), entry.getValue()));


		SpringApplication.run(WalletApplication.class, args);
	}

}
