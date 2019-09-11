package com.github.mtesmct.rieau.api.application;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.Optional;

import com.github.mtesmct.rieau.api.application.auth.AuthenticationService;
import com.github.mtesmct.rieau.api.application.dossiers.CerfaImportService;
import com.github.mtesmct.rieau.api.application.dossiers.ImporterCerfaService;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.CodePieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DossierFactory;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.StatutDossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypePieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Naissance;
import com.github.mtesmct.rieau.api.domain.entities.personnes.PersonnePhysique;
import com.github.mtesmct.rieau.api.domain.entities.personnes.PersonnePhysiqueId;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Sexe;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.domain.repositories.PersonnePhysiqueRepository;
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
public class ImporterCerfaServiceTests {
    @Autowired
    private DossierRepository dossierRepository;

    private ImporterCerfaService importerCerfaService;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private CerfaImportService cerfaImportService;
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
    private MockDateService dateRepository;
    @Autowired
    private PersonnePhysiqueRepository personnePhysiqueRepository;

    @Before
    public void setUp() throws Exception {
        this.importerCerfaService = new ImporterCerfaService(this.authenticationService, this.dossierFactory, this.dossierRepository, this.personnePhysiqueRepository,
                this.cerfaImportService);
        PersonnePhysique demandeur = new PersonnePhysique(new PersonnePhysiqueId("insee_01"), "jean.martin@monfai.fr", "Martin", "Jean", Sexe.HOMME, new Naissance(this.dateConverter.parse("01/01/1900 00:00:00"), "44000"));
		this.jpaSpringPersonnePhysiqueRepository.save(this.jpaPersonnePhysiqueFactory.toJpa(demandeur));
		this.dossier = this.dossierFactory.creer(demandeur);
        PieceJointe cerfa = new PieceJointe(new CodePieceJointe(TypePieceJointe.CERFA, null), new File("test"));
		this.dossierRepository.save(this.dossier);
        PersonnePhysique autreDemandeur = new PersonnePhysique(new PersonnePhysiqueId("insee_02"), "jacques.dupont@monfai.fr", "Dupont", "Jacques", Sexe.HOMME, new Naissance(this.dateConverter.parse("01/01/1900 00:00:00"), "44000"));
		PieceJointe autreCerfa = new PieceJointe(new CodePieceJointe(TypePieceJointe.CERFA, null), new File("test2"));
		this.otherDossier = this.dossierFactory.creer(autreDemandeur);
        this.dossierRepository.save(this.otherDossier);
    }

    
    @Test
    @WithDemandeurAndBetaDetails
    public void executeDPTest() {
        File file = new File("src/test/fixtures/cerfa_13703_DPMI.pdf");
        Optional<Dossier> dossier = this.importerCerfaService.execute(file);
        assertThat(dossier.isPresent(), is(true));
        assertThat(dossier.get().cerfa().code().type(), is(TypePieceJointe.DP));
        assertThat(dossier.get().statut(), is(StatutDossier.INSTRUCTION));
        assertThat(dossier.get().dateDepot(), is(this.dateRepository.now()));
        assertThat(dossier.get().piecesJointesObligatoires(), notNullValue());
    }

    @Test
    @WithDemandeurAndBetaDetails
    public void executePCMITest() {
        File file = new File("src/test/fixtures/cerfa_13406_PCMI.pdf");
        Optional<Dossier> dossier = this.importerCerfaService.execute( file);
        assertThat(dossier.isPresent(), is(true));
        assertThat(dossier.get().cerfa().code().type(), is(TypePieceJointe.PCMI));
        assertThat(dossier.get().statut(), is(StatutDossier.INSTRUCTION));
        assertThat(dossier.get().dateDepot(), is(this.dateRepository.now()));
        assertThat(dossier.get().piecesJointesObligatoires(), notNullValue());
    }
}