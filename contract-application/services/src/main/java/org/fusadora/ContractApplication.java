package org.fusadora;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "org.fusadora.model.datacontract") // Adjust the package name as needed
public class ContractApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContractApplication.class, args);
    }

}
