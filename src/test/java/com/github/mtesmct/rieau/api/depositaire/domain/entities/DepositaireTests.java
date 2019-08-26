package com.github.mtesmct.rieau.api.depositaire.domain.entities;

import static org.junit.Assert.assertThat;

import java.util.Optional;

import com.github.mtesmct.rieau.api.depositaire.domain.entities.Depot.Type;
import com.github.mtesmct.rieau.api.depositaire.domain.repositories.DepotRepository;
import com.github.mtesmct.rieau.api.depositaire.domain.repositories.IdDepotRepository;
import com.github.mtesmct.rieau.api.depositaire.infra.date.DateConverter;
import com.github.mtesmct.rieau.api.depositaire.infra.date.MockDateRepository;
import com.github.mtesmct.rieau.api.depositaire.infra.http.WithDepositaireAndBetaDetails;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.collection.IsEmptyCollection.empty;

@RunWith(SpringRunner.class)
@SpringBootTest
@WithDepositaireAndBetaDetails
public class DepositaireTests {
	@Autowired
	private DepotRepository depotRepository;
	@Autowired
	private IdDepotRepository idDepotRepository;
    
    private Depositaire depositaire;
    private MockDateRepository dateRepository;

    @Autowired
    @Qualifier("dateTimeConverter")
	private DateConverter converter;

    @Before
    public void setUp() throws Exception {
        this.dateRepository = new MockDateRepository(this.converter,"01/01/2019 00:00:00");
        this.depositaire = new Depositaire(this.depotRepository, dateRepository, idDepotRepository);
    }

    @Test
    @WithDepositaireAndBetaDetails
    public void depose() {
        Optional<Depot> optionalDepot = this.depositaire.ajouterDepot(Type.dp);
        assertThat(optionalDepot.isPresent(), is(true));
        Depot depot = optionalDepot.get();
        assertThat(this.depositaire.listeMesDepots(), not(empty()));
        assertThat(this.depositaire.listeMesDepots().size(), is(1));
        assertThat(this.depositaire.listeMesDepots().get(0), notNullValue());
        assertThat(this.depositaire.listeMesDepots().get(0).getEtat(), is(depot.getEtat()));
        assertThat(this.depositaire.listeMesDepots().get(0).getDate().compareTo(this.dateRepository.now()), is(0));
        assertThat(this.depositaire.trouveMonDepot(depot.getId()).isPresent(), is(true));
        assertThat(this.depositaire.trouveMonDepot(depot.getId()).get(), equalTo(this.depositaire.listeMesDepots().get(0)));
    }

}