package com.github.mtesmct.rieau.api.infra.application.dossiers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserServiceException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypesDossier;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.factories.DossierFactory;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.domain.services.DateService;
import com.github.mtesmct.rieau.api.infra.application.auth.WithDeposantAndBetaDetails;
import com.github.mtesmct.rieau.api.infra.application.dossiers.TxListerMesDossiersService;
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
@WithDeposantAndBetaDetails
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TxListerMesDossiersServiceTests {
    @Autowired
    private DossierRepository dossierRepository;

    @Autowired
    private TxListerMesDossiersService listerMesDossiersService;
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

    @BeforeEach
    public void setUp() throws Exception {
        this.dossier = this.dossierFactory.creer(this.deposantBeta, TypesDossier.DP);
        this.dossierRepository.save(this.dossier);
    }

    @Test
    @WithDeposantAndBetaDetails
    public void executeTest() throws AuthRequiredException, UserForbiddenException, UserServiceException {
        assertNotNull(this.listerMesDossiersService);
        assertFalse(this.listerMesDossiersService.execute().isEmpty());
        assertEquals(this.listerMesDossiersService.execute().size(), 1);
        assertNotNull(this.listerMesDossiersService.execute().get(0));
        assertEquals(this.listerMesDossiersService.execute().get(0).identity(), this.dossier.identity());
        assertEquals(this.listerMesDossiersService.execute().get(0).statut(), this.dossier.statut());
        assertEquals(this.listerMesDossiersService.execute().get(0).dateDepot().compareTo(this.dateService.now()), 0);
    }
}