package org.fusadora.contract;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "org.fusadora.model")
public class ContractApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContractApplication.class, args);
	}

}
