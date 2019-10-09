package com.github.mtesmct.rieau.api.infra.application.dossiers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.Optional;

import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.AuthenticationService;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DeposantForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.ParcelleCadastrale;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Projet;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypesDossier;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.factories.DossierFactory;
import com.github.mtesmct.rieau.api.domain.factories.ProjetFactory;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.infra.application.auth.WithDeposantBetaDetails;
import com.github.mtesmct.rieau.api.infra.application.auth.WithMairieBetaDetails;
import com.github.mtesmct.rieau.api.infra.date.DateConverter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TxConsulterDossierServiceTests {
    @MockBean
    private DossierRepository dossierRepository;

    @Autowired
    private TxConsulterDossierService consulterDossierService;
    @Autowired
    private DossierFactory dossierFactory;
    @Autowired
    private ProjetFactory projetFactory;

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
    @Autowired
    private AuthenticationService authenticationService;

    @BeforeEach
    public void setUp() throws Exception {
        Projet projet = this.projetFactory.creer("1", "rue des Lilas", "ZA des Fleurs", "44100", "BP 44", "Cedex 01",
                new ParcelleCadastrale("0", "1", "2"), true, true);
        this.dossier = this.dossierFactory.creer(this.deposantBeta, TypesDossier.DP, projet);
        Mockito.when(this.dossierRepository.save(any())).thenReturn(this.dossier);
        this.dossier = this.dossierRepository.save(this.dossier);
        assertNotNull(this.dossier);
        assertNotNull(this.dossier.identity());
        assertNotNull(this.dossier.deposant());
        assertTrue(this.dossier.pieceJointes().isEmpty());
        this.otherDossier = this.dossierFactory.creer(this.instructeurNonBeta, TypesDossier.DP, projet);
        Mockito.when(this.dossierRepository.save(any())).thenReturn(this.otherDossier);
        this.otherDossier = this.dossierRepository.save(this.otherDossier);
        assertNotNull(this.otherDossier);
        assertNotNull(this.otherDossier.identity());
        assertNotNull(this.otherDossier.deposant());
        assertTrue(this.otherDossier.pieceJointes().isEmpty());
    }

    @Test
    @WithDeposantBetaDetails
    public void executeDeposantTest()
            throws DeposantForbiddenException, AuthRequiredException, UserForbiddenException, UserInfoServiceException {
        Mockito.when(this.dossierRepository.findById(anyString())).thenReturn(Optional.ofNullable(this.dossier));
        Optional<Dossier> dossierTrouve = this.consulterDossierService.execute(this.dossier.identity().toString());
        assertTrue(dossierTrouve.isPresent());
        assertEquals(this.dossier, dossierTrouve.get());
    }

    @Test
    @WithMairieBetaDetails
    public void executeMairieTest()
            throws DeposantForbiddenException, AuthRequiredException, UserForbiddenException, UserInfoServiceException {
        Mockito.when(this.dossierRepository.findById(anyString())).thenReturn(Optional.ofNullable(this.dossier));
        Optional<Dossier> dossierTrouve = this.consulterDossierService.execute(this.dossier.identity().toString());
        assertTrue(dossierTrouve.isPresent());
        assertEquals(this.dossier, dossierTrouve.get());
    }
    
    @Test
    @WithDeposantBetaDetails
    public void executeDeposantAutreDossierTestInterdit() throws Exception {
        Mockito.when(this.dossierRepository.findById(anyString())).thenReturn(Optional.ofNullable(this.otherDossier));
        assertNotNull(this.otherDossier.deposant());
        Optional<Personne> user = this.authenticationService.user();
        assertTrue(user.isPresent());
        assertFalse(this.otherDossier.deposant().equals(user.get()));
        assertThrows(DeposantForbiddenException.class, () -> this.consulterDossierService.execute(this.otherDossier.identity().toString()));
    }
}