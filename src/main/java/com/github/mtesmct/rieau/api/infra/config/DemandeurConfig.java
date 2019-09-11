package com.github.mtesmct.rieau.api.infra.config;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.DossierFactory;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DossierIdService;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.domain.repositories.PersonnePhysiqueRepository;
import com.github.mtesmct.rieau.api.application.AuthenticationService;
import com.github.mtesmct.rieau.api.application.CerfaImportService;
import com.github.mtesmct.rieau.api.application.DemandeurService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemandeurConfig {
	@Autowired
    private DossierRepository dossierRepository;
	@Autowired
    private PersonnePhysiqueRepository personnePhysiqueRepository;
	@Autowired
    private DossierIdService dossierIdService;
	@Autowired
    private CerfaImportService cerfaImportService;
	@Autowired
    private AuthenticationService authenticationService;

    @Bean
    public DemandeurService demandeurService(){
        return new DemandeurService(this.authenticationService, this.dossierFactory(), this.dossierRepository, this.personnePhysiqueRepository, this.cerfaImportService);
    }

    @Bean
    public DossierFactory dossierFactory(){
        return new DossierFactory(this.dossierIdService);
    }
}
