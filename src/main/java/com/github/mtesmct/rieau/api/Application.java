package com.github.mtesmct.rieau.api;

import com.github.mtesmct.rieau.api.infra.InfraPackageScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackageClasses =  {InfraPackageScan.class})
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
