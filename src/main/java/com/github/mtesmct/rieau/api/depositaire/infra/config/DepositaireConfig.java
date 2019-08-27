package com.github.mtesmct.rieau.api.depositaire.infra.config;

import com.github.mtesmct.rieau.api.depositaire.domain.entities.Depositaire;
import com.github.mtesmct.rieau.api.depositaire.domain.repositories.DepotRepository;
import com.github.mtesmct.rieau.api.depositaire.domain.services.CerfaAdapter;
import com.github.mtesmct.rieau.api.depositaire.domain.services.CerfaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DepositaireConfig {

	@Autowired
    private DepotRepository depotRepository;
	@Autowired
    private CerfaService cerfaService;
	@Autowired
    private CerfaAdapter cerfaAdapter;

    @Bean
    public Depositaire depositaire(){
        return new Depositaire(this.depotRepository, this.cerfaService, this.cerfaAdapter);
    }
}
