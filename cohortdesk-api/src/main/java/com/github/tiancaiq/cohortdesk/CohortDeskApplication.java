package com.github.tiancaiq.cohortdesk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
//@SpringBootApplication
@SpringBootApplication(scanBasePackages = "com.github.tiancaiq")
public class CohortDeskApplication {

	public static void main(String[] args) {
		SpringApplication.run(CohortDeskApplication.class, args);
	}

}
