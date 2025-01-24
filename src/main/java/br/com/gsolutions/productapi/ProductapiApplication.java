package br.com.gsolutions.productapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;


@SpringBootApplication
public class ProductapiApplication {

	public static void main(String[] args) {

		SpringApplication.run(ProductapiApplication.class, args);
	}

}
