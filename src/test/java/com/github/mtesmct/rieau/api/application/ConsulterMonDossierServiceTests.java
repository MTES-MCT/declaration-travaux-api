package com.github.mtesmct.rieau.api.application;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import com.github.mtesmct.rieau.api.application.auth.AuthenticationService;
import com.github.mtesmct.rieau.api.application.auth.AuthorizationService;
import com.github.mtesmct.rieau.api.application.dossiers.ConsulterMonDossierService;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DossierFactory;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Naissance;
import com.github.mtesmct.rieau.api.domain.entities.personnes.PersonnePhysique;
import com.github.mtesmct.rieau.api.domain.entities.personnes.PersonnePhysiqueId;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Sexe;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.infra.date.DateConverter;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.factories.JpaPersonnePhysiqueFactory;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.repositories.JpaSpringPersonnePhysiqueRepository;
import com.github.mtesmct.rieau.api.infra.security.WithDeposantAndBetaDetails;

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
@WithDeposantAndBetaDetails
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ConsulterMonDossierServiceTests {
    @Autowired
    private DossierRepository dossierRepository;

    private ConsulterMonDossierService consulterMonDossierService;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private DossierFactory dossierFactory;

    @Autowired
    @Qualifier("dateTimeConverter")
    private DateConverter dateConverter;

    private Dossier dossier;
    
    private Dossier otherDossier;

	@Autowired
	private JpaPersonnePhysiqueFactory jpaPersonnePhysiqueFactory;

	@Autowired
	private JpaSpringPersonnePhysiqueRepository jpaSpringPersonnePhysiqueRepository;

    @Before
    public void setUp() throws Exception {
        this.consulterMonDossierService = new ConsulterMonDossierService(this.authenticationService, this.authorizationService, this.dossierRepository);
        PersonnePhysique deposant = new PersonnePhysique(new PersonnePhysiqueId("insee_01"), "jean.martin@monfai.fr", "Martin", "Jean", Sexe.HOMME, new Naissance(this.dateConverter.parse("01/01/1900 00:00:00"), "44000"));
		this.jpaSpringPersonnePhysiqueRepository.save(this.jpaPersonnePhysiqueFactory.toJpa(deposant));
		this.dossier = this.dossierFactory.creer(deposant);
        this.dossierRepository.save(this.dossier);
        PersonnePhysique autreDeposant = new PersonnePhysique(new PersonnePhysiqueId("insee_02"), "jacques.dupont@monfai.fr", "Dupont", "Jacques", Sexe.HOMME, new Naissance(this.dateConverter.parse("01/01/1900 00:00:00"), "44000"));
		this.otherDossier = this.dossierFactory.creer(autreDeposant);
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
        assertThat(this.consulterMonDossierService.execute(this.otherDossier.identity().toString()).isEmpty(), is(true));
     }
}