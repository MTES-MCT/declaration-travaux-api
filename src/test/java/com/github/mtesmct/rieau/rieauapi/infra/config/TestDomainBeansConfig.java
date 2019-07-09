package com.github.mtesmct.rieau.rieauapi.infra.config;

import com.github.mtesmct.rieau.rieauapi.domain.entities.Depositaire;
import com.github.mtesmct.rieau.rieauapi.domain.repositories.DepositaireRepository;
import com.github.mtesmct.rieau.rieauapi.infra.date.FakeDateRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class TestDomainBeansConfig {

	@Autowired
	private DepositaireRepository depositaireRepository;

	@Bean
	public Depositaire depositaire(@Value("${date.format}") String formatDate, @Value("${fake.date}") String fakeDate) {
		FakeDateRepository dateRepository = new FakeDateRepository(formatDate, fakeDate);
		return new Depositaire(this.depositaireRepository, dateRepository);
	}
}