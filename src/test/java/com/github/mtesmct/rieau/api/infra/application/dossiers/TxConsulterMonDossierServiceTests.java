package com.github.mtesmct.rieau.api.infra.application.dossiers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.DeposantNonAutoriseException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypesDossier;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.factories.DossierFactory;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.infra.application.auth.WithDeposantAndBetaDetails;
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
public class TxConsulterMonDossierServiceTests {
    @Autowired
    private DossierRepository dossierRepository;

    @Autowired
    private TxConsulterMonDossierService consulterMonDossierService;
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

    @BeforeEach
    public void setUp() throws Exception {
        this.dossier = this.dossierFactory.creer(this.deposantBeta, TypesDossier.DP);
        this.dossier = this.dossierRepository.save(this.dossier);
        assertNotNull(this.dossier);
        assertNotNull(this.dossier.identity());
        assertNotNull(this.dossier.deposant());
        assertTrue(this.dossier.pieceJointes().isEmpty());
        this.otherDossier = this.dossierFactory.creer(this.instructeurNonBeta, TypesDossier.DP);
        this.otherDossier = this.dossierRepository.save(this.otherDossier);
        assertNotNull(this.otherDossier);
        assertNotNull(this.otherDossier.identity());
        assertNotNull(this.otherDossier.deposant());
        assertTrue(this.otherDossier.pieceJointes().isEmpty());
    }

    @Test
    @WithDeposantAndBetaDetails
    public void executeTest() {
        Optional<Dossier> dossierTrouve = this.consulterMonDossierService.execute(this.dossier.identity().toString());
        assertTrue(dossierTrouve.isPresent());
        assertEquals(this.dossier, dossierTrouve.get());
    }
    
    @Test
    @WithDeposantAndBetaDetails
    public void executeAutreDossierTestInterdit() throws Exception {
        assertThrows(DeposantNonAutoriseException.class, () -> this.consulterMonDossierService.execute(this.otherDossier.identity().toString()));
    }
}