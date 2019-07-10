package com.github.mtesmct.rieau.api.infra.config;

import com.github.mtesmct.rieau.api.domain.entities.Depositaire;
import com.github.mtesmct.rieau.api.domain.repositories.DateRepository;
import com.github.mtesmct.rieau.api.domain.repositories.DemandeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DepositaireConfig {

	@Autowired
    private DateRepository dateRepository;

	@Autowired
    private DemandeRepository demandeRepository;

    @Bean
    public Depositaire depositaire(){
        return new Depositaire(this.demandeRepository, this.dateRepository);
    }
}
