package com.github.mtesmct.rieau.api.application;

import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.Optional;

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
import com.github.mtesmct.rieau.api.infra.date.MockDateRepository;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.factories.JpaPersonnePhysiqueFactory;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.repositories.JpaSpringPersonnePhysiqueRepository;
import com.github.mtesmct.rieau.api.infra.security.WithDepositaireAndBetaDetails;

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
@WithDepositaireAndBetaDetails
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DemandeurServiceTests {
    @Autowired
    private DossierRepository dossierRepository;
    @Autowired
    private PersonnePhysiqueRepository personnePhysiqueRepository;

    private DemandeurService demandeurService;
    @Autowired
    private CerfaImportService cerfaImportService;
    @Autowired
    private AuthenticationService authenticationService;
    private MockDateRepository dateRepository;
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
        this.dateRepository = new MockDateRepository(this.dateConverter, "01/01/2019 00:00:00");
        this.demandeurService = new DemandeurService(this.authenticationService, this.dossierFactory, this.dossierRepository, this.personnePhysiqueRepository,
                this.cerfaImportService);
        PersonnePhysique demandeur = new PersonnePhysique(new PersonnePhysiqueId("insee_01"), "jean.martin@monfai.fr", "Martin", "Jean", Sexe.HOMME, new Naissance(this.dateConverter.parse("01/01/1900 00:00:00"), "44000"));
		this.jpaSpringPersonnePhysiqueRepository.save(this.jpaPersonnePhysiqueFactory.toJpa(demandeur));
		PieceJointe cerfa = new PieceJointe(new CodePieceJointe(TypePieceJointe.CERFA, null), new File("test"));
		this.dossier = this.dossierFactory.creer(demandeur);
        this.dossierRepository.save(this.dossier);
        PersonnePhysique autreDemandeur = new PersonnePhysique(new PersonnePhysiqueId("insee_02"), "jacques.dupont@monfai.fr", "Dupont", "Jacques", Sexe.HOMME, new Naissance(this.dateConverter.parse("01/01/1900 00:00:00"), "44000"));
		PieceJointe autreCerfa = new PieceJointe(new CodePieceJointe(TypePieceJointe.CERFA, null), new File("test2"));
		this.otherDossier = this.dossierFactory.creer(autreDemandeur);
        this.dossierRepository.save(this.otherDossier);
    }

    @Test
    @WithDepositaireAndBetaDetails
    public void listeTest() {
        assertThat(this.demandeurService.liste(), not(empty()));
        assertThat(this.demandeurService.liste().size(), is(1));
        assertThat(this.demandeurService.liste().get(0), notNullValue());
        assertThat(this.demandeurService.liste().get(0).identity(), is(this.dossier.identity()));
        assertThat(this.demandeurService.liste().get(0).statut(), is(this.dossier.statut()));
        assertThat(
                this.demandeurService.liste().get(0).dateDepot().compareTo(this.dateRepository.now()),
                is(0));
    }

    @Test
    @WithDepositaireAndBetaDetails
    public void donneTest() {
        assertThat(this.demandeurService.donne( this.dossier.identity().toString()).isPresent(), is(true));
        assertThat(this.demandeurService.liste(), not(empty()));
        assertThat(this.demandeurService.donne( this.dossier.identity().toString()).get(),
                equalTo(this.demandeurService.liste().get(0)));
    }
    @Test
    @WithDepositaireAndBetaDetails
    public void donneAutreDossierTest() {
        assertThat(this.demandeurService.donne( this.otherDossier.identity().toString()).isEmpty(), is(true));
     }

    @Test
    @WithDepositaireAndBetaDetails
    public void importeDPTest() {
        File file = new File("src/test/fixtures/cerfa_13703_DPMI.pdf");
        Optional<Dossier> dossier = this.demandeurService.importe( file);
        assertThat(dossier.isPresent(), is(true));
        assertThat(dossier.get().cerfa().code().type(), is(TypePieceJointe.DP));
        assertThat(dossier.get().statut(), is(StatutDossier.INSTRUCTION));
        assertThat(dossier.get().dateDepot(), is(this.dateRepository.now()));
        assertThat(this.demandeurService.donne( dossier.get().identity().toString()).get(), equalTo(dossier.get()));
        assertThat(dossier.get().piecesJointesObligatoires(), notNullValue());
    }

    @Test
    @WithDepositaireAndBetaDetails
    public void importePCMITest() {
        File file = new File("src/test/fixtures/cerfa_13406_PCMI.pdf");
        Optional<Dossier> dossier = this.demandeurService.importe( file);
        assertThat(dossier.isPresent(), is(true));
        assertThat(dossier.get().cerfa().code().type(), is(TypePieceJointe.PCMI));
        assertThat(dossier.get().statut(), is(StatutDossier.INSTRUCTION));
        assertThat(dossier.get().dateDepot(), is(this.dateRepository.now()));
        assertThat(this.demandeurService.donne( dossier.get().identity().toString()).get(), equalTo(dossier.get()));
    }

}