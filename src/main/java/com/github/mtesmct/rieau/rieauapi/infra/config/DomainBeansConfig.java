package com.github.mtesmct.rieau.rieauapi.infra.config;

import com.github.mtesmct.rieau.rieauapi.domain.entities.Depositaire;
import com.github.mtesmct.rieau.rieauapi.domain.repositories.DateRepository;
import com.github.mtesmct.rieau.rieauapi.domain.repositories.DepositaireRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("production")
public class DomainBeansConfig {

	@Autowired
    private DateRepository dateRepository;

	@Autowired
    private DepositaireRepository repository;

    @Bean
    public Depositaire depositaire(){
        return new Depositaire(this.repository, this.dateRepository);
    }
}
