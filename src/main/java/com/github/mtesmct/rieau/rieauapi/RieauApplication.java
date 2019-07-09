package com.github.mtesmct.rieau.rieauapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class RieauApplication {

	/**
	 * Améliore le temps de chargement pour les tests
	 */
	@Configuration
    @Profile("test")
	@ComponentScan(lazyInit = true)
    static class LazyConfig {
    }

	public static void main(String[] args) {
		SpringApplication.run(RieauApplication.class, args);
	}

}
