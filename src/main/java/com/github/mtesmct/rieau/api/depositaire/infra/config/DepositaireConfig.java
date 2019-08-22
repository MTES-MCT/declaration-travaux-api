package com.github.mtesmct.rieau.api.depositaire.infra.config;

import com.github.mtesmct.rieau.api.depositaire.domain.entities.Depositaire;
import com.github.mtesmct.rieau.api.depositaire.domain.repositories.DateRepository;
import com.github.mtesmct.rieau.api.depositaire.domain.repositories.DepotRepository;
import com.github.mtesmct.rieau.api.depositaire.domain.repositories.IdDepotRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DepositaireConfig {

	@Autowired
    private DateRepository dateRepository;

	@Autowired
    private DepotRepository depotRepository;

	@Autowired
    private IdDepotRepository idDepotRepository;

    @Bean
    public Depositaire depositaire(){
        return new Depositaire(this.depotRepository, this.dateRepository, this.idDepotRepository);
    }
}
