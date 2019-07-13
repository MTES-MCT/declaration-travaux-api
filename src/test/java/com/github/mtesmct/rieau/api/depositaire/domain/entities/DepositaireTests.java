package com.github.mtesmct.rieau.api.depositaire.domain.entities;

import static org.junit.Assert.assertThat;

import com.github.mtesmct.rieau.api.depositaire.domain.repositories.DemandeRepository;
import com.github.mtesmct.rieau.api.depositaire.infra.date.DateConverter;
import com.github.mtesmct.rieau.api.depositaire.infra.date.MockDateRepository;
import com.github.mtesmct.rieau.api.depositaire.infra.http.WithDepositaireDetails;

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
@WithDepositaireDetails
public class DepositaireTests {
	@Autowired
	private DemandeRepository demandeRepository;
    
    private Depositaire depositaire;
    private MockDateRepository dateRepository;

    @Autowired
    @Qualifier("dateTimeConverter")
	private DateConverter converter;

    @Before
    public void setUp() throws Exception {
        this.dateRepository = new MockDateRepository(this.converter,"01/01/2019 00:00:00");
        this.depositaire = new Depositaire(this.demandeRepository, dateRepository);
    }

    @Test
    @WithDepositaireDetails
    public void depose() {
        Demande demande = new Demande("0", "dp", "instruction", this.dateRepository.now());
        this.depositaire.depose(demande);
        assertThat(this.depositaire.listeMesDemandes(), not(empty()));
        assertThat(this.depositaire.listeMesDemandes().size(), is(1));
        assertThat(this.depositaire.listeMesDemandes().get(0), notNullValue());
        assertThat(this.depositaire.listeMesDemandes().get(0).getEtat(), is(demande.getEtat()));
        assertThat(this.depositaire.listeMesDemandes().get(0).getDate().compareTo(this.dateRepository.now()), is(0));
        assertThat(this.depositaire.trouveMaDemande(demande.getId()).isPresent(), is(true));
        assertThat(this.depositaire.trouveMaDemande(demande.getId()).get(), equalTo(this.depositaire.listeMesDemandes().get(0)));
    }

}