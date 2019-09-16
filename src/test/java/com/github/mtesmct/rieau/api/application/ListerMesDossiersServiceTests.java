package com.github.mtesmct.rieau.api.application;

import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DossierFactory;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeDossier;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.domain.services.DateService;
import com.github.mtesmct.rieau.api.infra.application.auth.WithDeposantAndBetaDetails;
import com.github.mtesmct.rieau.api.infra.application.dossiers.SpringListerMesDossiersService;
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
public class ListerMesDossiersServiceTests {
    @Autowired
    private DossierRepository dossierRepository;

    @Autowired
    private SpringListerMesDossiersService listerMesDossiersService;
    @Autowired
    private DateService dateService;
    @Autowired
    private DossierFactory dossierFactory;

    @Autowired
    @Qualifier("dateTimeConverter")
    private DateConverter dateConverter;

    private Dossier dossier;
    @Autowired
    @Qualifier("deposantBeta")
    private Personne deposantBeta;

    @Before
    public void setUp() throws Exception {
        this.dossier = this.dossierFactory.creer(this.deposantBeta, null, TypeDossier.DP);
        this.dossierRepository.save(this.dossier);
    }

    @Test
    @WithDeposantAndBetaDetails
    public void executeTest() {
        assertThat(this.listerMesDossiersService, notNullValue());
        assertThat(this.listerMesDossiersService.execute(), not(empty()));
        assertThat(this.listerMesDossiersService.execute().size(), is(1));
        assertThat(this.listerMesDossiersService.execute().get(0), notNullValue());
        assertThat(this.listerMesDossiersService.execute().get(0).identity(), is(this.dossier.identity()));
        assertThat(this.listerMesDossiersService.execute().get(0).statut(), is(this.dossier.statut()));
        assertThat(
                this.listerMesDossiersService.execute().get(0).dateDepot().compareTo(this.dateService.now()),
                is(0));
    }
}