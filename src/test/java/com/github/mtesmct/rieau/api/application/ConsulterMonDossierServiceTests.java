package com.github.mtesmct.rieau.api.application;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DossierFactory;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeDossier;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.infra.application.auth.WithDeposantAndBetaDetails;
import com.github.mtesmct.rieau.api.infra.application.dossiers.SpringConsulterMonDossierService;
import com.github.mtesmct.rieau.api.infra.date.DateConverter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@WithDeposantAndBetaDetails
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ConsulterMonDossierServiceTests {
    @Autowired
    private DossierRepository dossierRepository;

    @Autowired
    private SpringConsulterMonDossierService consulterMonDossierService;
    @Autowired
    private DossierFactory dossierFactory;

    @Autowired
    @Qualifier("dateTimeConverter")
    private DateConverter dateConverter;

    private Dossier dossier;

    private Dossier otherDossier;
    @Autowired
    @Qualifier("deposantBeta")
    private Personne deposantBeta;
    @Autowired
    @Qualifier("instructeurNonBeta")
    private Personne instructeurNonBeta;

    @Before
    public void setUp() throws Exception {
        this.dossier = this.dossierFactory.creer(this.deposantBeta, null, TypeDossier.DP);
        this.dossierRepository.save(this.dossier);
        this.otherDossier = this.dossierFactory.creer(this.instructeurNonBeta, null, TypeDossier.DP);
        this.dossierRepository.save(this.otherDossier);
    }

    @Test
    @WithDeposantAndBetaDetails
    public void executeTest() {
        assertThat(this.consulterMonDossierService.execute(this.dossier.identity().toString()).isPresent(), is(true));
    }

    @Test
    @WithDeposantAndBetaDetails
    public void executeAutreDossierTest() {
        assertThat(this.consulterMonDossierService.execute(this.otherDossier.identity().toString()).isEmpty(),
                is(true));
    }
}