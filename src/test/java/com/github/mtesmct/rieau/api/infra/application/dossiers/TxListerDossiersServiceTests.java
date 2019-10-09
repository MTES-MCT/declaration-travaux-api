package com.github.mtesmct.rieau.api.infra.application.dossiers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.ParcelleCadastrale;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Projet;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypesDossier;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.factories.DossierFactory;
import com.github.mtesmct.rieau.api.domain.factories.ProjetFactory;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.domain.services.DateService;
import com.github.mtesmct.rieau.api.infra.application.auth.WithDeposantBetaDetails;
import com.github.mtesmct.rieau.api.infra.application.auth.WithMairieBetaDetails;
import com.github.mtesmct.rieau.api.infra.date.DateConverter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@WithDeposantBetaDetails
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TxListerDossiersServiceTests {
    @Autowired
    private DossierRepository dossierRepository;

    @Autowired
    private TxListerDossiersService listerDossiersService;
    @Autowired
    private DateService dateService;
    @Autowired
    private DossierFactory dossierFactory;
	@Autowired
	private ProjetFactory projetFactory;

    @Autowired
    @Qualifier("dateTimeConverter")
    private DateConverter dateConverter;

    private Dossier dossier;
    @Autowired
    @Qualifier("deposantBeta")
    private Personne deposantBeta;

    @BeforeEach
    public void setUp() throws Exception {
        Projet projet = this.projetFactory.creer("1", "rue des Lilas", "ZA des Fleurs", "44100", "BP 44", "Cedex 01", new ParcelleCadastrale("0","1","2"), true, true);
        this.dossier = this.dossierFactory.creer(this.deposantBeta, TypesDossier.DP, projet);
        this.dossierRepository.save(this.dossier);
    }

    @Test
    @WithDeposantBetaDetails
    public void executeDeposantTest() throws AuthRequiredException, UserForbiddenException, UserInfoServiceException {
        assertNotNull(this.listerDossiersService);
        assertFalse(this.listerDossiersService.execute().isEmpty());
        assertEquals(this.listerDossiersService.execute().size(), 1);
        assertNotNull(this.listerDossiersService.execute().get(0));
        assertEquals(this.listerDossiersService.execute().get(0).identity(), this.dossier.identity());
        assertEquals(this.listerDossiersService.execute().get(0).statut(), this.dossier.statut());
        assertEquals(this.listerDossiersService.execute().get(0).dateDepot().compareTo(this.dateService.now()), 0);
    }
    @Test
    @WithMairieBetaDetails
    public void executeMairieTest() throws AuthRequiredException, UserForbiddenException, UserInfoServiceException {
        assertNotNull(this.listerDossiersService);
        assertFalse(this.listerDossiersService.execute().isEmpty());
        assertEquals(this.listerDossiersService.execute().size(), 1);
        assertNotNull(this.listerDossiersService.execute().get(0));
        assertEquals(this.listerDossiersService.execute().get(0).identity(), this.dossier.identity());
        assertEquals(this.listerDossiersService.execute().get(0).statut(), this.dossier.statut());
        assertEquals(this.listerDossiersService.execute().get(0).dateDepot().compareTo(this.dateService.now()), 0);
    }
}