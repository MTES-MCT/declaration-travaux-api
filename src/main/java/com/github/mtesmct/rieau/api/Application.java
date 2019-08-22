package com.github.mtesmct.rieau.api;

import com.github.mtesmct.rieau.api.depositaire.infra.PackageScan;
import com.github.mtesmct.rieau.api.depositaire.infra.persistence.jpa.repositories.JpaNaturalRepository;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackageClasses =  {PackageScan.class})
@EnableJpaRepositories(repositoryBaseClass = JpaNaturalRepository.class)
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
