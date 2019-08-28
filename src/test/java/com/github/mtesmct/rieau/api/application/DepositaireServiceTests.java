package com.github.mtesmct.rieau.api.application;

import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.security.Principal;
import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.adapters.CerfaAdapter;
import com.github.mtesmct.rieau.api.domain.entities.Depot;
import com.github.mtesmct.rieau.api.domain.entities.Depot.Etat;
import com.github.mtesmct.rieau.api.domain.entities.Depot.Type;
import com.github.mtesmct.rieau.api.domain.repositories.DepotRepository;
import com.github.mtesmct.rieau.api.infra.date.DateConverter;
import com.github.mtesmct.rieau.api.infra.date.MockDateRepository;
import com.github.mtesmct.rieau.api.infra.file.pdf.PdfCerfaAdapter;
import com.github.mtesmct.rieau.api.infra.http.WithDepositaireAndBetaDetails;

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
public class DepositaireServiceTests {
	@Autowired
	private DepotRepository depotRepository;
    
	private DepositaireService depositaireService;
    private CerfaAdapter cerfaAdapter;
    @Autowired
	private CerfaService cerfaService;
    private MockDateRepository dateRepository;
    @Autowired
    private NoNationalService noNationalService;

    @Autowired
    @Qualifier("dateTimeConverter")
    private DateConverter converter;
    
    private Depot depot;
    
    private Principal principal;

    @Before
    public void setUp() throws Exception {
        this.dateRepository = new MockDateRepository(this.converter,"01/01/2019 00:00:00");
        this.cerfaAdapter = new PdfCerfaAdapter(this.dateRepository, this.noNationalService);
        this.depositaireService = new DepositaireService(this.depotRepository, this.cerfaService, this.cerfaAdapter);
        this.depot = new Depot(this.noNationalService.getNew(), Type.dp, this.dateRepository.now(), principal.getName());
        this.depotRepository.save(this.depot);
    }

    @Test
    @WithDepositaireAndBetaDetails
    public void liste() {
        assertThat(this.depositaireService.liste(principal), not(empty()));
        assertThat(this.depositaireService.liste(principal).size(), is(1));
        assertThat(this.depositaireService.liste(principal).get(0), notNullValue());
        assertThat(this.depositaireService.liste(principal).get(0).getId(), is(this.depot.getId()));
        assertThat(this.depositaireService.liste(principal).get(0).getType(), is(this.depot.getType()));
        assertThat(this.depositaireService.liste(principal).get(0).getEtat(), is(this.depot.getEtat()));
        assertThat(this.depositaireService.liste(principal).get(0).getDate().compareTo(this.dateRepository.now()), is(0));
    }

    @Test
    @WithDepositaireAndBetaDetails
    public void donne() {
        assertThat(this.depositaireService.donne(principal, this.depot.getId()).isPresent(), is(true));
        assertThat(this.depositaireService.donne(principal, this.depot.getId()).get(), equalTo(this.depositaireService.liste(principal).get(0)));
    }

    @Test
    @WithDepositaireAndBetaDetails
    public void importeDP() {
        File file = new File("src/test/fixtures/cerfa_13703_DPMI.pdf");
        Optional<Depot> depot = this.depositaireService.importe(principal, file);
        assertThat(depot.isPresent(), is(true));
        assertThat(depot.get().getType(), is(Type.dp));
        assertThat(depot.get().getEtat(), is(Etat.instruction));
        assertThat(depot.get().getDate(), is(this.dateRepository.now()));
        assertThat(this.depositaireService.donne(principal, depot.get().getId()).get(), equalTo(depot.get()));
    }

    @Test
    @WithDepositaireAndBetaDetails
    public void importePCMI() {
        File file = new File("src/test/fixtures/cerfa_13406_PCMI.pdf");
        Optional<Depot> depot = this.depositaireService.importe(principal, file);
        assertThat(depot.isPresent(), is(true));
        assertThat(depot.get().getType(), is(Type.pcmi));
        assertThat(depot.get().getEtat(), is(Etat.instruction));
        assertThat(depot.get().getDate(), is(this.dateRepository.now()));
        assertThat(this.depositaireService.donne(principal, depot.get().getId()).get(), equalTo(depot.get()));
    }

}