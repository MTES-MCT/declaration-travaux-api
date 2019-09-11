package com.github.mtesmct.rieau.api.application;

import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import com.github.mtesmct.rieau.api.application.auth.AuthenticationService;
import com.github.mtesmct.rieau.api.application.auth.AuthorizationService;
import com.github.mtesmct.rieau.api.application.dossiers.ListerMesDossiersService;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DossierFactory;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Naissance;
import com.github.mtesmct.rieau.api.domain.entities.personnes.PersonnePhysique;
import com.github.mtesmct.rieau.api.domain.entities.personnes.PersonnePhysiqueId;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Sexe;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.infra.date.DateConverter;
import com.github.mtesmct.rieau.api.infra.date.MockDateService;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.factories.JpaPersonnePhysiqueFactory;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.repositories.JpaSpringPersonnePhysiqueRepository;
import com.github.mtesmct.rieau.api.infra.security.WithDemandeurAndBetaDetails;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"app.datetime.mock=01/01/2019 00:00:00"})
@WithDemandeurAndBetaDetails
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ListerMesDossiersServiceTests {
    @Autowired
    private DossierRepository dossierRepository;

    private ListerMesDossiersService listerMesDossiersService;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private AuthorizationService authorizationService;
    private MockDateService dateRepository;
    @Autowired
    private DossierFactory dossierFactory;

    @Autowired
    @Qualifier("dateTimeConverter")
    private DateConverter dateConverter;

    private Dossier dossier;

	@Autowired
	private JpaPersonnePhysiqueFactory jpaPersonnePhysiqueFactory;

	@Autowired
	private JpaSpringPersonnePhysiqueRepository jpaSpringPersonnePhysiqueRepository;

    @Before
    public void setUp() throws Exception {
        this.listerMesDossiersService = new ListerMesDossiersService(this.authenticationService, this.authorizationService, this.dossierRepository);
        PersonnePhysique demandeur = new PersonnePhysique(new PersonnePhysiqueId("insee_01"), "jean.martin@monfai.fr", "Martin", "Jean", Sexe.HOMME, new Naissance(this.dateConverter.parse("01/01/1900 00:00:00"), "44000"));
		this.jpaSpringPersonnePhysiqueRepository.save(this.jpaPersonnePhysiqueFactory.toJpa(demandeur));
		this.dossier = this.dossierFactory.creer(demandeur);
        this.dossierRepository.save(this.dossier);
    }

    @Test
    @WithDemandeurAndBetaDetails
    public void listeTest() {
        assertThat(this.listerMesDossiersService.execute(), not(empty()));
        assertThat(this.listerMesDossiersService.execute().size(), is(1));
        assertThat(this.listerMesDossiersService.execute().get(0), notNullValue());
        assertThat(this.listerMesDossiersService.execute().get(0).identity(), is(this.dossier.identity()));
        assertThat(this.listerMesDossiersService.execute().get(0).statut(), is(this.dossier.statut()));
        assertThat(
                this.listerMesDossiersService.execute().get(0).dateDepot().compareTo(this.dateRepository.now()),
                is(0));
    }
}