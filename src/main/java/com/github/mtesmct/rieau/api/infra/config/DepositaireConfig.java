package com.github.mtesmct.rieau.api.infra.config;

import com.github.mtesmct.rieau.api.domain.adapters.CerfaAdapter;
import com.github.mtesmct.rieau.api.domain.repositories.DepotRepository;
import com.github.mtesmct.rieau.api.application.CerfaService;
import com.github.mtesmct.rieau.api.application.DepositaireService;

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
    public DepositaireService depositaire(){
        return new DepositaireService(this.depotRepository, this.cerfaService, this.cerfaAdapter);
    }
}
