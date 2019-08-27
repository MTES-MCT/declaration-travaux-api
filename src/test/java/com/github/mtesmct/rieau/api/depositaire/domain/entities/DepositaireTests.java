package com.github.mtesmct.rieau.api.depositaire.domain.entities;

import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.Optional;

import com.github.mtesmct.rieau.api.depositaire.domain.entities.Depot.Etat;
import com.github.mtesmct.rieau.api.depositaire.domain.entities.Depot.Type;
import com.github.mtesmct.rieau.api.depositaire.domain.repositories.DepotRepository;
import com.github.mtesmct.rieau.api.depositaire.domain.services.CerfaAdapter;
import com.github.mtesmct.rieau.api.depositaire.domain.services.CerfaService;
import com.github.mtesmct.rieau.api.depositaire.domain.services.NoNationalService;
import com.github.mtesmct.rieau.api.depositaire.infra.date.DateConverter;
import com.github.mtesmct.rieau.api.depositaire.infra.date.MockDateRepository;
import com.github.mtesmct.rieau.api.depositaire.infra.http.WithDepositaireAndBetaDetails;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.collection.IsEmptyCollection.empty;

@RunWith(SpringRunner.class)
@SpringBootTest
@WithDepositaireAndBetaDetails
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DepositaireTests {
	@Autowired
	private DepotRepository depotRepository;
    
	private Depositaire depositaire;
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

    @Before
    public void setUp() throws Exception {
        this.dateRepository = new MockDateRepository(this.converter,"01/01/2019 00:00:00");
        this.cerfaAdapter = new CerfaAdapter(this.dateRepository, this.noNationalService);
        this.depositaire = new Depositaire(this.depotRepository, this.cerfaService, this.cerfaAdapter);
        this.depot = new Depot(this.noNationalService.getNew(), Type.dp, this.dateRepository.now());
        this.depotRepository.save(this.depot);
    }

    @Test
    @WithDepositaireAndBetaDetails
    public void listeMesDepots() {
        assertThat(this.depositaire.listeMesDepots(), not(empty()));
        assertThat(this.depositaire.listeMesDepots().size(), is(1));
        assertThat(this.depositaire.listeMesDepots().get(0), notNullValue());
        assertThat(this.depositaire.listeMesDepots().get(0).getId(), is(this.depot.getId()));
        assertThat(this.depositaire.listeMesDepots().get(0).getType(), is(this.depot.getType()));
        assertThat(this.depositaire.listeMesDepots().get(0).getEtat(), is(this.depot.getEtat()));
        assertThat(this.depositaire.listeMesDepots().get(0).getDate().compareTo(this.dateRepository.now()), is(0));
    }

    @Test
    @WithDepositaireAndBetaDetails
    public void trouveMonDepot() {
        assertThat(this.depositaire.trouveMonDepot(this.depot.getId()).isPresent(), is(true));
        assertThat(this.depositaire.trouveMonDepot(this.depot.getId()).get(), equalTo(this.depositaire.listeMesDepots().get(0)));
    }

    @Test
    @WithDepositaireAndBetaDetails
    public void importerDepotDP() {
        File file = new File("src/test/fixtures/cerfa_13703_DPMI.pdf");
        Optional<Depot> depot = this.depositaire.importerDepot(file);
        assertThat(depot.isPresent(), is(true));
        assertThat(depot.get().getType(), is(Type.dp));
        assertThat(depot.get().getEtat(), is(Etat.instruction));
        assertThat(depot.get().getDate(), is(this.dateRepository.now()));
        assertThat(this.depositaire.trouveMonDepot(depot.get().getId()).get(), equalTo(depot.get()));
    }

    @Test
    @WithDepositaireAndBetaDetails
    public void importerDepotPCMI() {
        File file = new File("src/test/fixtures/cerfa_13406_PCMI.pdf");
        Optional<Depot> depot = this.depositaire.importerDepot(file);
        assertThat(depot.isPresent(), is(true));
        assertThat(depot.get().getType(), is(Type.pcmi));
        assertThat(depot.get().getEtat(), is(Etat.instruction));
        assertThat(depot.get().getDate(), is(this.dateRepository.now()));
        assertThat(this.depositaire.trouveMonDepot(depot.get().getId()).get(), equalTo(depot.get()));
    }

}