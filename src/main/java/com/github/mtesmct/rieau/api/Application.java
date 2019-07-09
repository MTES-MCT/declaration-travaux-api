package com.github.mtesmct.rieau.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@EnableAutoConfiguration
public class Application {

	/**
	 * Améliore le temps de chargement pour les tests
	 */
	@Configuration
    @Profile("test")
	@ComponentScan(lazyInit = true)
    static class LazyConfig {
    }

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
