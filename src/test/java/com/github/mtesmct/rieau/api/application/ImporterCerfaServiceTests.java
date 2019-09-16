package com.github.mtesmct.rieau.api.application;

import static org.hamcrest.core.Is.is;
// import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.StatutDossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeDossier;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.services.DateService;
import com.github.mtesmct.rieau.api.infra.application.auth.WithDeposantAndBetaDetails;
import com.github.mtesmct.rieau.api.infra.application.dossiers.SpringImporterCerfaService;
import com.github.mtesmct.rieau.api.infra.date.DateConverter;

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
public class ImporterCerfaServiceTests {
    @Autowired
    private SpringImporterCerfaService importerCerfaService;

    @Autowired
    @Qualifier("dateTimeConverter")
    private DateConverter dateConverter;

	@Autowired
    private DateService dateService;
    @Autowired
    @Qualifier("deposantBeta")
    private Personne deposantBeta;
    
    @Test
    @WithDeposantAndBetaDetails
    public void executeDPTest() {
        File file = new File("src/test/fixtures/cerfa_13703_DPMI.pdf");
        Optional<Dossier> dossier = this.importerCerfaService.execute(file);
        assertThat(dossier.isPresent(), is(true));
        assertThat(dossier.get().type(), is(TypeDossier.DP));
        assertThat(dossier.get().statut(), is(StatutDossier.DEPOSE));
        assertThat(dossier.get().dateDepot(), is(this.dateService.now()));
        // assertThat(dossier.get().piecesJointesObligatoires(), notNullValue());
        assertThat(dossier.get().deposant().identity().toString(), is(this.deposantBeta.identity().toString()));
    }

    @Test
    @WithDeposantAndBetaDetails
    public void executePCMITest() {
        File file = new File("src/test/fixtures/cerfa_13406_PCMI.pdf");
        Optional<Dossier> dossier = this.importerCerfaService.execute(file);
        assertThat(dossier.isPresent(), is(true));
        assertThat(dossier.get().type(), is(TypeDossier.PCMI));
        assertThat(dossier.get().statut(), is(StatutDossier.DEPOSE));
        assertThat(dossier.get().dateDepot(), is(this.dateService.now()));
        // assertThat(dossier.get().piecesJointesObligatoires(), notNullValue());
        assertThat(dossier.get().deposant().identity().toString(), is(this.deposantBeta.identity().toString()));
    }
}