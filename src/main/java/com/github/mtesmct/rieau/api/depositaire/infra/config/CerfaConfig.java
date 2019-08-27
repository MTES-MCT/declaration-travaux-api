package com.github.mtesmct.rieau.api.depositaire.infra.config;

import com.github.mtesmct.rieau.api.depositaire.domain.repositories.DateRepository;
import com.github.mtesmct.rieau.api.depositaire.domain.services.CerfaAdapter;
import com.github.mtesmct.rieau.api.depositaire.domain.services.NoNationalService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CerfaConfig {

	@Autowired
    private DateRepository dateRepository;
	@Autowired
    private NoNationalService noNationalService;

    @Bean
    public CerfaAdapter cerfaAdapter(){
        return new CerfaAdapter(this.dateRepository, this.noNationalService);
    }
}
